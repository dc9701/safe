package tools.vpm;

import tools.vpm.StackInfo.Stack;
import tools.vpm.PrinterRegistration.SupportedPrinterFamily;

import tools.vpm.VirtualPrinter.VirtualPrinterStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;

public class VirtualPrinterPool {

	private static String poolPath;
	private static boolean initiated = false;
	
	public static void init(String vPMDataPath){
		poolPath=vPMDataPath + "virtualPrinterPool.txt";
		initiated = true;
	}

	public static VirtualPrinter getPrinterFromPool(SupportedPrinterFamily printerFamily, VirtualPrinterStatus status, Stack stack) {
		if(!initiated) throw new RuntimeException("Need to call init() first!");
		if (printerFamily == null) {
			printerFamily = SupportedPrinterFamily.MORGANI;
		}
		if (status == null) {
			status = VirtualPrinterStatus.ONLINE;
		}

		VirtualPrinter targetPrinter = null;
		String targetPrinterStack = (stack != null) ? stack.toString() : Stack.DEV2.toString();
		String targetPrinterModelNumber = PrinterRegistration.getPrinterRegistration(printerFamily, 43200).getModelNumber();
		String targetPrinterState = null;
		if (status == VirtualPrinterStatus.ONLINE)
			targetPrinterState = "Connected";
		if (status == VirtualPrinterStatus.OFFLINE)
			targetPrinterState = "Disconnected";

		RandomAccessFile raf = null;
		FileChannel channel = null;
		FileLock lock = null;
		ArrayList<String> printerList = new ArrayList<String>();

		try {
			File file = new File(poolPath);
			if (!file.exists())
				file.createNewFile();
			file = null;
			raf = new RandomAccessFile(poolPath, "rw");
			channel = raf.getChannel();
			lock = channel.lock();

			while (raf.getFilePointer() < raf.length()) {
				String printerDetails = raf.readLine();
				if (printerDetails != null) {
					printerList.add(printerDetails);
				}
			}

			for (String printerDetails : printerList) {

				VirtualPrinter currentPrinter = convertStringToPrinter(printerDetails);
				if (currentPrinter.getModelNumber().equalsIgnoreCase(targetPrinterModelNumber)
						&& currentPrinter.getPrinterState().equalsIgnoreCase(targetPrinterState)
						&& currentPrinter.getStack().equalsIgnoreCase(targetPrinterStack)) {
					printerList.remove(printerDetails);
					targetPrinter = currentPrinter;
					break;
				}
			}

			if (targetPrinter != null) {
				channel.truncate(0);
				for (String printerDetails : printerList) {
					ByteBuffer sendBuffer = ByteBuffer.wrap((printerDetails + "\n").getBytes());
					channel.write(sendBuffer);
				}
			} else {
				System.out.println("Cannot get printer from the printer pool, prepare to register new printer");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (lock != null) {
				try {
					lock.release();
					lock = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (channel != null) {
				try {
					channel.close();
					channel = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (raf != null) {
				try {
					raf.close();
					raf = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}// end finally
		return targetPrinter;
	}

	public static void savePrinterToPool(VirtualPrinter targetPrinter) {
		if(!initiated) throw new RuntimeException("Need to call init() first!");
		if (targetPrinter == null) {
			System.out.println("There is no printer to save to the printer!");
			return;
		}

		RandomAccessFile raf = null;
		FileChannel channel = null;
		FileLock lock = null;
		String printerDetails = convertPrinterToString(targetPrinter);

		try {
			raf = new RandomAccessFile(poolPath, "rw");
			channel = raf.getChannel();
			lock = channel.lock();
			raf.seek(raf.length());
			ByteBuffer sendBuffer = ByteBuffer.wrap((printerDetails + "\n").getBytes());
			channel.write(sendBuffer);
			System.out.println("Save Printer : \" " + targetPrinter.getEmailID() + " \" to the printer pool successfully!");
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (lock != null) {
				try {
					lock.release();
					lock = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (channel != null) {
				try {
					channel.close();
					channel = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (raf != null) {
				try {
					raf.close();
					raf = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}// end finally
	}

	private static VirtualPrinter convertStringToPrinter(String printerDetails) {
		
		VirtualPrinter printer = new VirtualPrinter();

		String printerProperties[] = printerDetails.split(";");

		for (String properties : printerProperties) {

			String item[] = properties.split(":");
			String itemName = item[0].trim();
			String itemValue = item[1].trim();

			if (itemName.equalsIgnoreCase("modelNumber")) {
				printer.setModelNumber(itemValue);
			} else if (itemName.equalsIgnoreCase("claimCode")) {
				printer.setClaimCode(itemValue);
			} else if (itemName.equalsIgnoreCase("printerEmailId")) {
				printer.setEmailID(itemValue);
			} else if (itemName.equalsIgnoreCase("stack")) {
				printer.setStack(itemValue);
			} else if (itemName.equalsIgnoreCase("printerId")) {
				printer.setPrinterId(itemValue);
			} else if (itemName.equalsIgnoreCase("printerState")) {
				printer.setPrinterState(itemValue);
			} else if (itemName.equalsIgnoreCase("serialNumber")) {
				printer.setSerialNumber(itemValue);
			} else {
				System.out.println("The property : \" " + itemName + " \" is not one of the printer properties!");
			}
		}

		return printer;
	}

	private static String convertPrinterToString(VirtualPrinter printer) {
		
		String printerProperties = null;

		if (printer != null) {
			printerProperties = "stack : " + printer.getStack() + " ; " + "modelNumber : " + printer.getModelNumber() + " ; " + "claimCode : "
					+ printer.getClaimCode() + " ; " + "printerEmailId : " + printer.getEmailID() + " ; " + "printerId : " + printer.getPrinterId() + " ; "
					+ "printerState : " + printer.getPrinterState() + " ; " + "serialNumber : " + printer.getSerialNumber();
		} else {
			System.out.println("The printer is null!");
		}

		return printerProperties;
	}


}
