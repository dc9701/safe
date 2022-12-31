package tools.vpm;

public class VirtualPrinter {
	public static enum VirtualPrinterStatus {
		ONLINE, OFFLINE
	}

	private String claimCode;
	private String deRegisterUrl;
	private String listJobUrl;
	private String modelNumber;
	private String printerActiveTime;
	private String emailID;
	private String printerId;
	private String printerKey;
	private String printerSetUID;
	private String printerState;
	private String printerType;
	private String registrationPage;
	private String responseTime;
	private String serialNumber;
	private String xmpp;
	private String stack;

	public String getClaimCode() {
		return claimCode;
	}

	public void setClaimCode(String claimCode) {
		this.claimCode = claimCode;
	}

	public String getDeRegisterUrl() {
		return deRegisterUrl;
	}

	public void setDeRegisterUrl(String deRegisterUrl) {
		this.deRegisterUrl = deRegisterUrl;
	}

	public String getListJobUrl() {
		return listJobUrl;
	}

	public void setListJobUrl(String listJobUrl) {
		this.listJobUrl = listJobUrl;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getPrinterActiveTime() {
		return printerActiveTime;
	}

	public void setPrinterActiveTime(String printerActiveTime) {
		this.printerActiveTime = printerActiveTime;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getPrinterId() {
		return printerId;
	}

	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}

	public String getPrinterKey() {
		return printerKey;
	}

	public void setPrinterKey(String printerKey) {
		this.printerKey = printerKey;
	}

	public String getPrinterSetUID() {
		return printerSetUID;
	}

	public void setPrinterSetUID(String printerSetUID) {
		this.printerSetUID = printerSetUID;
	}

	public String getPrinterState() {
		return printerState;
	}

	public void setPrinterState(String printerState) {
		this.printerState = printerState;
	}

	public String getPrinterType() {
		return printerType;
	}

	public void setPrinterType(String printerType) {
		this.printerType = printerType;
	}

	public String getRegistrationPage() {
		return registrationPage;
	}

	public void setRegistrationPage(String registrationPage) {
		this.registrationPage = registrationPage;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getXmpp() {
		return xmpp;
	}

	public void setXmpp(String xmpp) {
		this.xmpp = xmpp;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

}
