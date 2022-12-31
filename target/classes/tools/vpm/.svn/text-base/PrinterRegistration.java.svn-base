package tools.vpm;

/**
 * Printer registration class
 */
public class PrinterRegistration {
	public static SupportedPrinterFamily defaultFamily;
	public static int defaultDuration;
	public static PrinterRegistration defaultPrinterRegistration;

	private String modelName;
	private String modelNumber;
	private String family;
	private String protocol;
	private int duration;

	/**
	 * This enum type contains all kinds of supported printers, currently supports 72 different
	 * models.
	 */

	public static enum SupportedPrinterFamily {
		MargaritaI, MargaritaII, MargaritaIII, MargaritaIV, MargaritaV, CoulombI, CoulombII, CoulombIII, CoulombIV, CoulombV, STUTTGARTI, STUTTGARTII, STUTTGARTIII, STUTTGARTIV, CesarI, CesarII, CesarIII, EpsilonI, EpsilonII, EpsilonIII, EpsilonIV, EpsilonV, EpsilonVI, EpsilonVIII, EpsilonIX, EpsilonXI, EpsilonXII, EpsilonXIII, EpsilonXIV, EpsilonXV, EpsilonXVI, EpsilonXVII, EpsilonXVIII, EpsilonXIX, EpsilonXX, EpsilonL, EpsilonLII, EpsilonLIII, EpsilonLIV, EpsilonPlus, PokerI, PokerII, PokerIII, PokerIV, MORGANI, MORGANII, MORGANIII, MORGANIV, MORGANV, MORGAN_PHOTOI, MORGAN_PHOTOII, MORGAN_PHOTOIII, Tequila, Messenger, Ampere, Lorentz_BASE, Lorentz_Mid_High, Whtiney, Tau, Tassen, TassenPlus, Eddelman, Zelig, VaderMLK, Syrah, FORESTER, SID, Mantis, EAGLE, LonePine, LEHMAN, Cougar, Mykonos, Mykonos_PLUS, Ochid,
	}

	

	/**
	 * Constructor
	 * Set printer's family, modelNumber, modelName, protocol and duration by given parameters.
	 */

	public PrinterRegistration(String modelName, String modelNumber, String family, String protocol, int duration) {
		this.modelName = modelName;
		this.modelNumber = modelNumber;
		this.family = family;
		this.protocol = protocol;
		this.duration = duration;
	}

	/**
	 * Constructor
	 * Set printer's family, modelNumber, modelName, protocol and duration by given printerFamily
	 * and duration.
	 */

	public PrinterRegistration(SupportedPrinterFamily printerFamily, int duration) {

		switch (printerFamily) {

		case MargaritaI:

			this.family = "MargaritaI";
			this.modelNumber = "CZ155A";
			this.modelName = "Officejet_6600_e_All_in_One";
			this.protocol = "ledm";
			break;

		case MargaritaII:

			this.family = "MargaritaII";
			this.modelNumber = "CZ160A";
			this.modelName = "Officejet_6600_e_All_in_One";
			this.protocol = "ledm";
			break;

		case MargaritaIII:

			this.family = "MargaritaIII";
			this.modelNumber = "CZ161A";
			this.modelName = "Officejet_6600_e_All_in_One";
			this.protocol = "ledm";
			break;

		case MargaritaIV:

			this.family = "MargaritaIV";
			this.modelNumber = "CN583A";
			this.modelName = "Officejet_6700_Premium_e_All_in_One";
			this.protocol = "ledm";
			break;

		case MargaritaV:

			this.family = "MargaritaV";
			this.modelNumber = "CV078A";
			this.modelName = "Officejet_6700_Premium_e_All_in_One";
			this.protocol = "ledm";
			break;

		case CoulombI:

			this.family = "CoulombI";
			this.modelNumber = "CM749A";
			this.modelName = "Officejet_Pro_8600_N911a";
			this.protocol = "ledm";
			break;

		case CoulombII:

			this.family = "CoulombII";
			this.modelNumber = "CN578A";
			this.modelName = "Officejet_Pro_8600_N911a";
			this.protocol = "ledm";
			break;

		case CoulombIII:

			this.family = "CoulombIII";
			this.modelNumber = "CM750A";
			this.modelName = "Officejet_Pro_8600_N911g";
			this.protocol = "ledm";
			break;

		case CoulombIV:

			this.family = "CoulombIV";
			this.modelNumber = "CN579A";
			this.modelName = "Officejet_Pro_8600_N911g";
			this.protocol = "ledm";
			break;

		case CoulombV:

			this.family = "CoulombV";
			this.modelNumber = "CN577A";
			this.modelName = "Officejet_Pro_8600_N911n";
			this.protocol = "ledm";
			break;

		case STUTTGARTI:

			this.family = "STUTTGARTI";
			this.modelNumber = "CX052A";
			this.modelName = "Deskjet_3520_e_All_in_One";
			this.protocol = "ipp";
			break;

		case STUTTGARTII:

			this.family = "STUTTGARTII";
			this.modelNumber = "CX056A";
			this.modelName = "Deskjet_3520_e_All_in_One";
			this.protocol = "ipp";
			break;

		case STUTTGARTIII:

			this.family = "STUTTGARTIII";
			this.modelNumber = "CX057A";
			this.modelName = "Deskjet_3520_e_All_in_One";
			this.protocol = "ipp";
			break;

		case STUTTGARTIV:

			this.family = "STUTTGARTIV";
			this.modelNumber = "CX058A";
			this.modelName = "Deskjet_3520_e_All_in_One";
			this.protocol = "ipp";
			break;

		case CesarI:

			this.family = "CesarI";
			this.modelNumber = "CZ152A";
			this.modelName = "Officejet_4620_series";
			this.protocol = "ledm";
			break;

		case CesarII:

			this.family = "CesarII";
			this.modelNumber = "CZ294A";
			this.modelName = "Officejet_4620_series";
			this.protocol = "ledm";
			break;

		case CesarIII:

			this.family = "CesarIII";
			this.modelNumber = "CZ295A";
			this.modelName = "Officejet_4620_series";
			this.protocol = "ledm";
			break;

		case EpsilonI:

			this.family = "EpsilonI";
			this.modelNumber = "CQ762A";
			this.modelName = "Photosmart_6510_B211a";
			this.protocol = "ledm";
			break;

		case EpsilonII:

			this.family = "EpsilonII";
			this.modelNumber = "CQ763A";
			this.modelName = "Photosmart_6510_B211a";
			this.protocol = "ledm";
			break;

		case EpsilonIII:

			this.family = "EpsilonIII";
			this.modelNumber = "CQ764C";
			this.modelName = "Photosmart_6510_B211a";
			this.protocol = "ledm";
			break;

		case EpsilonIV:

			this.family = "EpsilonIV";
			this.modelNumber = "CQ761B";
			this.modelName = "Photosmart_6510_B211a";
			this.protocol = "ledm";
			break;

		case EpsilonV:

			this.family = "EpsilonV";
			this.modelNumber = "CQ761D";
			this.modelName = "Photosmart_6510_B211e";
			this.protocol = "ledm";
			break;

		case EpsilonVI:

			this.family = "EpsilonVI";
			this.modelNumber = "CQ761C";
			this.modelName = "Photosmart_6510_B211b";
			this.protocol = "ledm";
			break;

		case EpsilonVIII:

			this.family = "EpsilonVIII";
			this.modelNumber = "CQ179A";
			this.modelName = "Photosmart_5510_B111a";
			this.protocol = "ledm";
			break;

		case EpsilonIX:

			this.family = "EpsilonIX";
			this.modelNumber = "CQ180A";
			this.modelName = "Photosmart_5510_B111a";
			this.protocol = "ledm";
			break;

		case EpsilonXI:

			this.family = "EpsilonXI";
			this.modelNumber = "CQ184A";
			this.modelName = "Photosmart_5510_B111j";
			this.protocol = "ledm";
			break;

		case EpsilonXII:

			this.family = "EpsilonXII";
			this.modelNumber = "CQ176D";
			this.modelName = "Photosmart_5510_B111g";
			this.protocol = "ledm";
			break;

		case EpsilonXIII:

			this.family = "EpsilonXIII";
			this.modelNumber = "CQ181C";
			this.modelName = "Photosmart_5510_B111a";
			this.protocol = "ledm";
			break;

		case EpsilonXIV:

			this.family = "EpsilonXIV";
			this.modelNumber = "CQ182C";
			this.modelName = "Photosmart_5510_B111a";
			this.protocol = "ledm";
			break;

		case EpsilonXV:

			this.family = "EpsilonXV";
			this.modelNumber = "CQ176B";
			this.modelName = "Photosmart_5510_B111a";
			this.protocol = "ledm";
			break;

		case EpsilonXVI:

			this.family = "EpsilonXVI";
			this.modelNumber = "CQ176C";
			this.modelName = "Photosmart_5510_B111b";
			this.protocol = "ledm";
			break;

		case EpsilonXVII:

			this.family = "EpsilonXVII";
			this.modelNumber = "CQ177B";
			this.modelName = "Photosmart_5510_B111c";
			this.protocol = "ledm";
			break;

		case EpsilonXVIII:

			this.family = "EpsilonXVIII";
			this.modelNumber = "CQ177C";
			this.modelName = "Photosmart_5510_B111d";
			this.protocol = "ledm";
			break;

		case EpsilonXIX:

			this.family = "EpsilonXIX";
			this.modelNumber = "CQ178B";
			this.modelName = "Photosmart_5510_B111e";
			this.protocol = "ledm";
			break;

		case EpsilonXX:

			this.family = "EpsilonXX";
			this.modelNumber = "CQ178C";
			this.modelName = "Photosmart_5510_B111f";
			this.protocol = "ledm";
			break;

		case EpsilonL:

			this.family = "EpsilonL";
			this.modelNumber = "CQ176A";
			this.modelName = "Photosmart_5510_B111a";
			this.protocol = "ledm";
			break;

		case EpsilonLII:

			this.family = "EpsilonLII";
			this.modelNumber = "CQ183B";
			this.modelName = "Photosmart_5510_B111h";
			this.protocol = "ledm";
			break;

		case EpsilonLIII:

			this.family = "EpsilonLIII";
			this.modelNumber = "CQ183C";
			this.modelName = "Photosmart_5510_B111j";
			this.protocol = "ledm";
			break;

		case EpsilonLIV:

			this.family = "EpsilonLIV";
			this.modelNumber = "CQ183A";
			this.modelName = "Photosmart_5510d_B111h";
			this.protocol = "ledm";
			break;

		case EpsilonPlus:

			this.family = "EpsilonPlus";
			this.modelNumber = "CQ761A";
			this.modelName = "Photosmart_6510_B211a";
			this.protocol = "ipp";
			break;

		case PokerI:

			this.family = "PokerI";
			this.modelNumber = "CX017A";
			this.modelName = "Photosmart_6520_e_All_in_One_Printer_series";
			this.protocol = "ledm";
			break;

		case PokerII:

			this.family = "PokerII";
			this.modelNumber = "CX018A";
			this.modelName = "Photosmart_6520_e_All_in_One_Printer_series";
			this.protocol = "ledm";
			break;

		case PokerIII:

			this.family = "PokerIII";
			this.modelNumber = "CX020C";
			this.modelName = "Photosmart_6520_e_All_in_One_Printer_series";
			this.protocol = "ledm";
			break;

		case PokerIV:

			this.family = "PokerIV";
			this.modelNumber = "CX017B";
			this.modelName = "Photosmart_6520_e_All_in_One_Printer_series";
			this.protocol = "ledm";
			break;

		case MORGANI:

			this.family = "MORGANI";
			this.modelNumber = "CX042A";
			this.modelName = "HP_Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGANII:

			this.family = "MORGANII";
			this.modelNumber = "CX043A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGANIII:

			this.family = "MORGANIII";
			this.modelNumber = "CX044A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGANIV:

			this.family = "MORGANIV";
			this.modelNumber = "CX049A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGANV:

			this.family = "MORGANV";
			this.modelNumber = "CX050A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGAN_PHOTOI:

			this.family = "MORGAN_PHOTOI";
			this.modelNumber = "CZ304A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGAN_PHOTOII:

			this.family = "MORGAN_PHOTOII";
			this.modelNumber = "CZ305A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case MORGAN_PHOTOIII:

			this.family = "MORGAN_PHOTOIII";
			this.modelNumber = "CZ306A";
			this.modelName = "Photosmart_5520_series";
			this.protocol = "ipp";
			break;

		case Tequila:

			this.family = "Tequila";
			this.modelNumber = "CB863A";
			this.modelName = "Officejet_6100_ePrinter";
			this.protocol = "ledm";
			break;

		case Messenger:

			this.family = "Messenger";
			this.modelNumber = "CN728A";
			this.modelName = "DeskJet";
			this.protocol = "ledm";
			break;

		case Ampere:

			this.family = "Ampere";
			this.modelNumber = "CQ893A";
			this.modelName = "HP_Designjet_T520";
			this.protocol = "ipp";
			break;

		case Lorentz_BASE:

			this.family = "Lorentz_BASE";
			this.modelNumber = "CM755A";
			this.modelName = "HP_Officejet_Pro_8500_A910";
			this.protocol = "ledm";
			break;

		case Lorentz_Mid_High:

			this.family = "Lorentz_Mid_High";
			this.modelNumber = "CM758A";
			this.modelName = "HP_Officejet_Pro_8500_A910";
			this.protocol = "ledm";
			break;

		case Whtiney:

			this.family = "Whtiney";
			this.modelNumber = "CD055A";
			this.modelName = "Photosmart_D110a";
			this.protocol = "ledm";
			break;

		case Tau:

			this.family = "Tau";
			this.modelNumber = "CN731A";
			this.modelName = "HP_Photosmart_D110a";
			this.protocol = "ledm";
			break;

		case Tassen:

			this.family = "Tassen";
			this.modelNumber = "CN216A";
			this.modelName = "HP_Photosmart_Plus_B210_Series";
			this.protocol = "ledm";
			break;

		case TassenPlus:

			this.family = "TassenPlus";
			this.modelNumber = "CN503A";
			this.modelName = "HP_Photosmart_Prem_C310_Series";
			this.protocol = "ledm";
			break;

		case Eddelman:

			this.family = "Eddelman";
			this.modelNumber = "C9309A";
			this.modelName = "HP_Officejet_7500_E910";
			this.protocol = "ledm";
			break;

		case Zelig:

			this.family = "Zelig";
			this.modelNumber = "CN727A";
			this.modelName = "DeskJet";
			this.protocol = "ledm";
			break;

		case VaderMLK:

			this.family = "VaderMLK";
			this.modelNumber = "CQ523C";
			this.modelName = "HP_Photosmart_Prem_C410_Japan";
			this.protocol = "ledm";
			break;

		case Syrah:

			this.family = "Syrah";
			this.modelNumber = "CN555A";
			this.modelName = "HP_Officejet_6500_E710a_f";
			this.protocol = "ledm";
			break;

		case FORESTER:

			this.family = "FORESTER";
			this.modelNumber = "CR770A";
			this.modelName = "HP_Officejet_Pro_250z_MFP";
			this.protocol = "ipp";
			break;

		case SID:

			this.family = "SID";
			this.modelNumber = "CE914A";
			this.modelName = "HP_LaserJet_CP1025nw";
			this.protocol = "ipp";
			break;

		case Mantis:

			this.family = "Mantis";
			this.modelNumber = "CE749A";
			this.modelName = "HP_LaserJet_CP1025nw";
			this.protocol = "ipp";
			break;

		case EAGLE:

			this.family = "EAGLE";
			this.modelNumber = "CF040M";
			this.modelName = "HP_LaserJet_200_colorMFP_M275nw";
			this.protocol = "ipp";
			break;

		case LonePine:

			this.family = "LonePine";
			this.modelNumber = "CE538A";
			this.modelName = "HP_LaserJet_M1536dnf_MFP";
			this.protocol = "ipp";
			break;

		case LEHMAN:

			this.family = "LEHMAN";
			this.modelNumber = "CN598A";
			this.modelName = "HP_Officejet_Pro_PWA_FAILSAFE";
			this.protocol = "ipp";
			break;

		case Cougar:

			this.family = "Cougar";
			this.modelNumber = "CE861A";
			this.modelName = "HP_LaserJet_Thunderbird_Series";
			this.protocol = "ipp";
			break;

		case Mykonos:

			family = "Mykonos";
			modelNumber = "A9T80A";
			modelName = "HP_ENVY_4500_series";
			protocol = "ledm";
			break;

		case Mykonos_PLUS:

			family = "Mykonos_PLUS";
			modelNumber = "B4L03A";
			modelName = "HP_Officejet_4630_series";
			protocol = "ledm";
			break;

		case Ochid:

			family = "Ochid";
			modelNumber = "A9J40A";
			modelName = "HP_ENVY_5530_series";
			protocol = "ledm";
			break;

		default:
			throw new RuntimeException();
		}

		this.duration = duration;
	}

	/**
	 * Returns the printer's modelName
	 * 
	 * @return a string of modelName
	 */

	public String getModelName() {
		return modelName;
	}

	/**
	 * Returns the printer's modelNumber
	 * 
	 * @return a string of modelNumber
	 */

	public String getModelNumber() {
		return modelNumber;
	}

	/**
	 * Returns the printer's family
	 * 
	 * @return a string of family
	 */

	public String getFamily() {
		return family;
	}

	/**
	 * Returns the printer's protocol
	 * 
	 * @return a string of protocol
	 */

	public String getProtocol() {
		return protocol;
	}

	/**
	 * Returns the duration in seconds
	 * 
	 * @return a time in seconds
	 */

	public int getDuration() {
		return duration;
	}

	
	/**
	 * Returns a PrinterRegistration instance according to the given printerFamily.
	 * 
	 * @return a PrinterRegistration instance.
	 */

	public static PrinterRegistration getPrinterRegistration(SupportedPrinterFamily printerFamily, int duration) {
		return new PrinterRegistration(printerFamily, duration);
	}
}
