#!/usr/bin/perl -w

=head1 NAME

R2QC_LSR.pl - Publish Lifesaver or Rumble test results to Quality Center (ALM).

=head1 DESCRIPTION

Results2QC is a perl script that will publish test results data in XML format to Quality Center.

This particular version has been tuned to support the IPG/Lifesaver project in ALM, and also not to overwrite
any manually-entered test results in ALM.

Overall process:

1. 	Transform the tool-specific results xml to a standardized result.xml format.
	a.	While processing results.xml, note the cumulative Run/Test Execution status and time.
	b.  Update the Test Case cumulative info.
	c.	Create a testRun in the xml for the test case and all Design Steps.

2. 	For each test case, determine whether a test instance already exists in the appropriate Test Set
	in Quality Center (/Service Manager/AutoTests/{project_branch}.  If NOT:
	
	a. 	Add a Test Case to the Test Plan and get the Test ID (associate Test Case keywords, as
		applicable).
	b. 	Link the Test Case to it's associated RQ or CR (if applicable, based upon Test Name).
	c. 	Add an instance for the Test Case in the Test Set.
	
3. 	Optionally remove any old Design Steps from the Test Case and add the new Design Steps.  
	
4.	Update the Last Run Status and Duration for the Test Case in the QC Test Plan.

5.	Create a new Test Run and update the overall run Status.

6.	Add each Run Step to the Test Run and update the Status of the Run Step.

=head1 CONTACT INFO

David C. Cooper (david.c.cooper@hp.com), IWS - Cloud Solutions  (previously SWS - HP Connected SWS / BTO - Service Manager QA)

=head1 REVISION LOG

2014.04.09  1.16    Filter out "&" chars in error or failure text.  "&amp;"" for some reason doesn't parse
                    in the soap->call().

2014.04.03  1.15    Tuned for IPG/Lifesaver project in ALM and removed commented-out lines specific to
                    HP Connected.  Doesn't add run results for tests that fail because the test is not
                    yet implemented (placeholder tests).

2012.10.16  1.14    Added -testGoup argument and changed default -hhtpProxy and -qcProxy.

2012.09.14  1.13    Updated HPC field mapping and error handling for both stdout & stderr CDATA and empty result files.

2012.07.15	1.12	Changing field mapping in SOAP requests to match IPG:cloudservice (HPC) schema.

2011.09.07  1.11	Added -qcProxy and -qcLabRootNotPassed arguments.

2011.01.19  1.10	Replace whitespace and special characters in test/testset name with underscores.
									Handle paths with embedded '.' chars.

2010.12.06  1.09	Fixed AddSteps() to not remove previously-added test case steps.

2010.10.04	1.08	Updated revision log.

2010.10.04	1.07	Updated to point to (another) new QC Proxy server:  qcp1c.atlanta.hp.com and
									switched from http: to https: protocol (which now seems required on qcp1c).

2010.09.28	1.06	Updated to point to new QC Proxy server:  qcp1b.atlanta.hp.com.

2010.08.19	1.05	Added support for processing multiple matching files in a folder tree (the
									-searchPath and -searchFolders arguments.

2010.08.12	1.04	Corrected issue in creating design steps for new test cases.  Added -httpProxy 
									argument.
					
2010.08.10	1.03	Removed "demo mode" from Results2QC.		

2010.08.09	1.02	Updates to enable build integration.

2010.08.03	1.01	Addeded external sample XSL stylesheets.

2010.07.29	1.00	Initial revision.

=cut

use strict;
use lib '\p4\ServiceManager\tools\test\R2QC';

use SOAP::Lite;
use Switch;
use Time::HiRes qw( gettimeofday );

use MKDoc::XML::Encode;
use XML::LibXML;
use XML::LibXSLT;
use XML::XPath;

use Data::Dumper;

#---------------------------------------------------------------------------------------------------#
# Global variable definitions
#---------------------------------------------------------------------------------------------------#

# These are used to create our SOAP session with QC Proxy and are used by most methods:

my $uri 				= 'http://com.hp.qualitycenter.proxy.ws';
my $soap 				= '';		# SOAP object.
my $strClientId = '';		# QC client session ID.	

# Other variables:

my %Args = ();					# Hash of args that may be specified on command line.
my $LogLevel = 1;				# Enables logging of method success (2) or errors (1).

my $BP_Filter = "HP";			# BP filter required for IPG security.
	
#---------------------------------------------------------------------------------------------------#
# Subroutine declarations
#---------------------------------------------------------------------------------------------------#

# Import Subroutines

sub AddSteps;					# Add steps to a test run or a test case.
sub AddTestRun;					# Add a test run and run steps to QC for the current test.

# Query Subroutines

sub GetTestId;					# Returns the ID of the specified test.
sub GetTestInstanceId;			# Returns the ID of the specified test instance.
sub GetTestPlanFolderId;		# Returns the ID of the specified Test Plan folder.
sub GetTestSetId;				# Returns the ID of the specified Test Lab test set.

# Transform Subroutines

sub GetStyleSheet;				# Return XSLT stylesheet as XML string.
sub XformTestSuite;				# Transform a test suite from various XML formats to standard XPath format.

# Utility Subroutines

sub LogStatus;					# Status logging.
sub RunTime;					# Elapsed run time, in seconds.
sub SetArgs;					# Returns a hash of validated command-line arguments.
sub ShowUsage;					# Show the USAGE message.

# QCProxy - QCCommon Subroutines

sub QCCommonConnect;			# Login to QC proxy.
sub QCCommonDisconnect;			# Disconnect from QC proxy.
sub QCCommonUserEmailGet;		# Return email address for specified QC user name.
sub QCCommonDomainGet;			# Return QC domain name for the current session.
sub QCCommonProjectGet;			# Return QC project name for the current session.
sub QCCommonListAdd;			# Add an item to a list in QC.
sub QCCommonListRemove;			# Remove an item from a list in QC.
sub QCCommonQuery;				# Return a list of matching records from QC.

# QCProxy - Test Plan Subroutines

sub TestPlanFolderAdd;			# Add a new folder to the test plan.
sub TestPlanTestAdd;			# Add a new test case to a folder in the test plan.
sub TestPlanTestStepAdd;		# Add a new design step to a test case in the test plan.
sub TestPlanTestGet;			# Get the list of tests contained in a specified folder.
sub TestPlanTestDescriptionGet;	# Get the descriptions of all design steps for a test case.
sub TestPlanTestStepGet;		# Get the name, expected result, and description of all steps in a test.
sub TestPlanTestStepRemove;		# Remove a specified design step within a test case.
sub TestPlanTestStepRemoveAll;	# Remove all design steps for a test case.

# QCProxy - Test Lab Subroutines

sub TestLabTestInstanceCreate;	# Add a test instance to a test set in the test lab.
sub TestLabRunCreate;			# Add a test run for a single test case to a test set in the test lab.
sub TestLabRunStatusSet;		# Set the status of a test run.
sub TestLabRunStepCreate;		# Create a test step in a specified test run.
sub TestLabRunStepStatusSet;	# Set the status of a single test design step in a specified test run.

#---------------------------------------------------------------------------------------------------#
# Main body of program
#---------------------------------------------------------------------------------------------------#

RunTime();							# Initialize timer & connect to QC server.

eval {no warnings; %Args = (@ARGV);};

if (%Args) {				# Import test results into QC if all args are OK.

	if (eval { %Args = SetArgs(\%Args) } ) {

		# Create SOAP session with QC Proxy, now that we have -httpProxy defined.

		$soap = SOAP::Lite->uri($uri)->on_action( sub { join '/', $uri, $_[1] } )->proxy($Args{qcProxy},
			proxy => ['http' => $Args{httpProxy}]) || 
			die "Results2QC - Fatal error with QC Proxy URI.\n";
			
		$soap->autotype(0);				# SOAP envelope.			
		
		$strClientId = QCCommonConnect();

		# For each result file specified, parse and transform the test suite.
			
		unless ($strClientId eq '') {

			SearchFolders($Args{searchPath}, $Args{searchFolders});

			exit QCCommonDisconnect();
		}
	}
	else {					# Print USAGE if any required args are missing.
	
		ShowUsage();
	}
}

#---------------------------------------------------------------------------------------------------#
# Import Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# AddSteps - Add steps to a test run or a test case.
#
# Usage:	AddSteps($Test, [$RunId])
# Returns:	The number of steps added.
#
# Notes:	If $RunId is '' or omitted, will add steps to test case; otherwise, will add steps
#			to specified test run.
#---------------------------------------------------------------------------------------------------#
sub AddSteps
{
	my $Test 	= shift;				# Test case object.
	my $RunId 	= shift;				# Test run ID (may be blank or omitted).

	my $TestName 		= $Test->find('@testName')->string_value();
	my $TestScriptPath 	= $Test->find('@testScriptPath')->string_value();
	
	my $StepsAdded 			= 0;		# Count of total steps added.
	my $StepsPassed			= 0;		# Count of total Passed steps.
	my $StepsFailed			= 0;		# Count of total Failed steps.
	my $StepsNotCompleted 	= 0;		# Count of total Not Completed steps.
	my $StepsNoRun	 		= 0;		# Count of total No Run steps.
	my $StepsNA 			= 0;		# Count of total N/A steps.

	# QC can't tolerate whitespace or special characters in the test name or path.

	$TestName        =~ s/[^\d\w]/_/g;

	# Remove any extant steps for the test case.

    TestPlanTestStepRemoveAll($TestScriptPath, $TestName);
    # DCC - TestPlanTestStepRemoveAll($TestScriptPath, $TestName) unless $RunId;

	foreach my $Step ($Test->find('./testSteps/step')->get_nodelist) {

		my $StepDesc 	= $Step->string_value();
		my $StepName 	= $Step->find('@stepName')->string_value();
		my $StepStatus 	= $Step->find('@stepStatus')->string_value();
		
		if ($RunId) {					# Add steps to test run in Test Lab if $RunId is specified.

			unless (TestLabRunStepStatusSet($RunId, $StepName, $StepStatus)) {

				# DCC - $StepsAdded++;
				
				if ($StepStatus eq 'Passed')		{ $StepsPassed++ }
				if ($StepStatus eq 'Failed')		{ $StepsFailed++ }
				if ($StepStatus eq 'Not Completed')	{ $StepsNotCompleted++ }
				if ($StepStatus eq 'No Run')		{ $StepsNoRun++ }
				if ($StepStatus eq 'N/A')			{ $StepsNA++ }
			}
		}
		
		# Add steps of test case in Test Plan if no $RunId.
		
		my $StepXml = 
			'<?xml version="1.0" encoding="UTF-8"?><ROOT><DATA>' .
			'<DS_STEP_NAME>'	. $StepName	. '</DS_STEP_NAME>' .
			'<DS_DESCRIPTION>'	. $StepDesc	. '</DS_DESCRIPTION>' .	
			'</DATA></ROOT>';
		
		if (TestPlanTestStepAdd("$TestScriptPath", $TestName, $StepXml) == 0) {
			
			$StepsAdded++;
		}
	}

	# Print a message indicating how many steps were added (and result summary, if run steps).

	if ($RunId) {
		print "Results2QC (" . RunTime() ."s):  Added run $RunId for test $TestName with $StepsAdded step";
		if ($StepsAdded != 1) 	{ print 's ('; } else { print ' ('; }		
		if ($StepsPassed)		{ print " Passed=$StepsPassed"; }
		if ($StepsFailed)		{ print " Failed=$StepsFailed"; }
		if ($StepsNotCompleted)	{ print " NotCompleted=$StepsNotCompleted"; }
		if ($StepsNoRun)		{ print " NoRun=$StepsNoRun"; }
		if ($StepsNA)			{ print " N/A=$StepsNA"; }
		print " )\n";
	}
	else {
		print "Results2QC (" . RunTime() ."s):  Added $StepsAdded step";
		if ($StepsAdded != 1) { print 's'; }
		print " to test $TestName\n";
	}	

	return $StepsAdded;
}

#---------------------------------------------------------------------------------------------------#
# AddTestRun - Add a test run and run steps to QC for the current test.
#
# Usage:	AddTestRun($Test)
# Returns:	Run ID of new test run.
#
# Notes:	Will add a test instance and test case, if they don't already exist.
#---------------------------------------------------------------------------------------------------#
sub AddTestRun
{
	my $Test = shift;			# Test case object.

	# Determine the TestId and TestInstanceId (will create them, if needed), and get
	# the Test Set name, path & run status.

	my $TestId 			= GetTestId($Test);
	my $TestInstanceId 	= GetTestInstanceId($Test, $TestId);	

	my $TestSetName 	= $Test->find('@testSetName')->string_value();
	my $TestSetPath 	= $Test->find('@testSetPath')->string_value();
	
	my $RunStatus 		= $Test->find('./testRun/@runStatus')->string_value();

	# QC can't tolerate whitespace or special characters in the test set name or path.
		
	$TestSetName =~ s/[^\d\w]/_/g;

	# We will write non-passing test instances to the -qcLabRootNotPassed folder.  By default
	# is the same as -qcLabRoot, where the passing test instances go, but if a different folder
	# is specified for -qcLabRootNotPassed, non-passing test instances will be created there.

	unless ($RunStatus eq 'Passed') {
	
		$TestSetPath = $Test->find('@testSetNotPassedPath')->string_value();
	}
		
	# Build the SOAP message and create the new run.
	
	my $RunXml =
		'<?xml version="1.0" encoding="UTF-8"?><DATA>' .
		'<RN_RUN_NAME>' . $Test->find('./testRun/@runName')->string_value()			. '</RN_RUN_NAME>' .
		'<RN_DURATION>' . $Test->find('./testRun/@runDuration')->string_value()		. '</RN_DURATION>' .
		'</DATA>';

 	my $RunId = TestLabRunCreate($TestSetPath, $TestSetName, $TestId, $TestInstanceId, 
 		$RunStatus, $RunXml);

	if ($RunId) { AddSteps($Test, $RunId); }	# Add all run steps.

	return $RunId;
}

#---------------------------------------------------------------------------------------------------#
# Query Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# GetTestId - Returns the ID of the specified test.
#
# Usage:	GetTestId($Test)
# Returns:	Test ID if found or 0, if not.
#
# Notes:	1. 	Uses permanent cache of test IDs, %TestIdCache.
#			2. 	Will create the test case, if needed.
#			3. 	Stores the TestScriptPath in the TS_PATH (Script Path) field to speed queries.
#---------------------------------------------------------------------------------------------------#
{
	my %TestIdCache = ();		# Cache of test IDs.
	
	sub GetTestId
	{
		my $Test = shift;	# Test object.
	
		my $TestAdded		= 0;	# Test case was added to Test Plan.
		my $TestFolderId	= '';	# Parent test folder ID.
		my $TestId 			= '';	# Test case ID.
		
		my $TestName 		= $Test->find('@testName')->string_value();			# Test case name.
		my $TestScriptPath 	= $Test->find('@testScriptPath')->string_value();	# Test subject path.

		my $QueryResult	= '';					# Query result string (CSV format).
		my $CurrentPath	= $TestScriptPath;		# Current ScriptPath of the test case in QC.
		
		# QC can't tolerate whitespace or special characters in the test name or path.
		
		$TestName       =~ s/[^\d\w]/_/g;

		# Check cache for test by TestScriptPath\TestName; if not found, query QC.
	
		if (exists $TestIdCache{$TestScriptPath . '\\' . $TestName}) {

			$TestId = $TestIdCache{$TestScriptPath . '\\' . $TestName};
		}
		else {						
		
			# Query QC to find the test by ScriptPath & TestName (fast, ~2 secs).
		
			if ($QueryResult = QCCommonQuery("SELECT TS_TEST_ID FROM TEST WHERE " . 
				"TS_PATH = \'$TestScriptPath\' AND " .
				"TS_NAME    = \'$TestName\'", 0, 1)) {
									
				if ($QueryResult =~ m/\d+/s) { $TestId = $& } else { $TestId = ''}
			}
			
			unless ($TestId) {				
		
				# If not found, query QC again by folder ID & TestName (slow, 30+ secs).
			
				if ($TestFolderId = GetTestPlanFolderId($TestScriptPath)) {
				
					if ($QueryResult = 
						QCCommonQuery("SELECT TS_TEST_ID, TS_PATH AS TS_SCRIPT_PATH FROM TEST WHERE " . 
						"TS_SUBJECT = \'$TestFolderId\' AND " .
						"TS_NAME    = \'$TestName\'", 0, 1)) {

						if ($QueryResult =~ m/\d+/s) { $TestId = $& } else { $TestId = ''}
						if ($QueryResult =~ m/"(Subject\\.*)"/s) { $CurrentPath = $& } else { $CurrentPath = ''}
					}
					
					# If the test case doesn't exist in QC, create a new one.  Or if the test's 
					# ScriptPath doesn't match $TestScriptPath (the test has been moved), also
					# call TestPlanTestAdd() to update the existing test.

					unless ($TestId && ($CurrentPath eq $TestScriptPath)) {
										
						my $TestXml =
	 						'<?xml version="1.0" encoding="UTF-8"?><ROOT><DATA>' .
	 						'<TS_NAME>'		  	. "$TestName"											. '</TS_NAME>' .
	 						'<TS_PATH>' 	 	. "$TestScriptPath" 									. '</TS_PATH>' .
	 						'<TS_DESCRIPTION>'	. $Test->find('@testDescription')->string_value()		. '</TS_DESCRIPTION>' .
	 						'<TS_USER_23>' 	 	. $Test->find('@testModule')->string_value()			. '</TS_USER_23>' .
	 						'<TS_STATUS>' 	 	. $Test->find('@testStatus')->string_value()			. '</TS_STATUS>' .
	 						'<TS_EXEC_STATUS>'	. $Test->find('@testExecutionStatus')->string_value()	. '</TS_EXEC_STATUS>' .
	 						'<TS_USER_TEMPLATE_01>'		. "$BP_Filter"											. '</TS_USER_TEMPLATE_01>' .
	 						'</DATA></ROOT>';

 						$TestId = TestPlanTestAdd("$TestScriptPath", "$TestName", $TestXml);

						if ($TestId) {
						
							print "Results2QC (" . RunTime() ."s):  ";
							
							unless ($CurrentPath) {
								
								print "Added";
								$TestAdded++;
							} 
							else { 
								print "Updated ScriptPath of"; 
							}
							
							print " test case $TestId - $TestName\n";
						}
					}
				}
			}

			# Update test steps if test was newly-added or -updateSteps was specified.

			if ($TestAdded || (exists $Args{updateSteps})) { AddSteps($Test); };

			# Add test ID to cache.
			
			if ($TestId) { $TestIdCache{$TestScriptPath . '\\' . $TestName} = $TestId }
		}

		return $TestId;
	}
}

#---------------------------------------------------------------------------------------------------#
# GetTestInstanceId - Returns the ID of the specified test instance.
#
# Usage:	GetTestInstanceId($Test, $TestId)
# Returns:	Test ID if found or 0, if not.
#
# Notes:	1. Will create the test instance, if needed.
#			2. Will also create the test set, if needed.
#---------------------------------------------------------------------------------------------------#
{
	my %InstanceIdCache = ();		# Cache of test instance IDs.
	
	sub GetTestInstanceId
	{
		my $Test   	= shift;		# Test object.
		my $TestId	= shift;		# Test ID.

		my $InstanceId 	= '';		# Test instance ID.		
		my $TestSetId	= '';		# Test set ID.
		
		my $QueryResult	= '';		# Query result string (CSV format).
		
		my $TestName 	= $Test->find('@testName')->string_value();		# Test case name.				
		my $TestSetName = $Test->find('@testSetName')->string_value();	# Test set name.				
		my $TestSetPath = $Test->find('@testSetPath')->string_value();	# Test set path.

		# QC can't tolerate whitespace or special characters in the test set name or path.
		
		$TestName    =~ s/[^\d\w]/_/g;
		$TestSetName =~ s/[^\d\w]/_/g;

		# We will write non-passing test instances to the -qcLabRootNotPassed folder.  By default
		# is the same as -qcLabRoot, where the passing test instances go, but if a different folder
		# is specified for -qcLabRootNotPassed, non-passing test instances will be created there.

		my $TestExecutionStatus 	= $Test->find('@testExecutionStatus')->string_value();		# Test case status.

		unless ($TestExecutionStatus eq 'Passed') {
		
			$TestSetPath = $Test->find('@testSetNotPassedPath')->string_value();
		}
		
		# Check cache for test by TestSetPath\TestId; if not found, query QC.
	
		if (exists $InstanceIdCache{$TestSetPath . '\\' . $TestId}) {

			$InstanceId = $InstanceIdCache{$TestSetPath . '\\' . $TestId};
		}
		else {						
		
			# If not in cache, query QC by TestId & TestSetId.
		
			if ($TestSetId = GetTestSetId($TestSetPath, $TestSetName)) {
			
				if ($QueryResult = QCCommonQuery("SELECT TC_TESTCYCL_ID FROM TESTCYCL WHERE " . 
				"TC_TEST_ID = \'$TestId\' AND TC_CYCLE_ID = \'$TestSetId\'", 0, 1)) {
				
					if ($QueryResult =~ m/\d+/s) { $InstanceId = $& } else { $InstanceId = ''}
				}										
			}

			# If the test instance doesn't exist, create it (this will also create the test set and
			# any containing folders if they don't exist).

			unless ($InstanceId) {

				my $InstanceXml =
					'<?xml version="1.0" encoding="UTF-8"?><DATA>' . 
 					'<CY_DESCRIPTION>' 	. "$TestSetPath"											. '</CY_DESCRIPTION>' .
					'<CY_USER_TEMPLATE_01>'		. "$BP_Filter"												. '</CY_USER_TEMPLATE_01>' .
					'</DATA>';

				$InstanceId = TestLabTestInstanceCreate($TestSetPath, $TestSetName, $TestId, $InstanceXml);

				if ($InstanceId) {
				
					print "Results2QC (" . RunTime() ."s):  Added test instance $InstanceId for test $TestName to test set $TestSetName\n";
				}
			}

			# Add test instance ID to cache.
			
			if ($InstanceId) { $InstanceIdCache{$TestSetPath . '\\' . $TestId} = $InstanceId }
		}

		return $InstanceId;
	}
}

#---------------------------------------------------------------------------------------------------#
# GetTestPlanFolderId - Returns the ID of the specified Test Plan folder.
#
# Usage:	GetTestPlanFolderId($FolderPath)
# Returns:	Test Plan folder ID if found or 0, if not.
#
# Notes:	1.	Uses permanent cache of test IDs, %FolderIdCache.
#			2.	Will create the test plan folder, if needed.
#			3.	The Test Plan folder is the same for all tests in the suite.
#			4.	Test folders are stored in the ALL_LISTS table, not TEST (but the IDs are the same).
#---------------------------------------------------------------------------------------------------#
{
	my %FolderIdCache = ();		# Cache of test IDs.
	
	sub GetTestPlanFolderId
	{
		my $FolderPath = shift;		# Test Plan folder path.
	
		my $FolderId = 0;			# Current folder's ID.

		# Check whether folder ID is already in cache.
		
		if (exists $FolderIdCache{$FolderPath}) {

			$FolderId = $FolderIdCache{$FolderPath};
		}
		else {		
		
			# If not in cache, query QC for folder ID, walking through the folder tree.
	
			my @PathArray = split(/\\/, $FolderPath);
		
			my $ParentFolderId	= 0;		# Parent folder's ID.
			my $SubjectPath		= '';		# Path of current folder in tree.
			
			my $QueryResult		= '';		# Query result string (CSV format).
		
			foreach my $Path (@PathArray) {

				$ParentFolderId = $FolderId;		# Update the parent folder ID.
				
				# If the test folder already exists, retrieve it's ID.
			
				if ($QueryResult = QCCommonQuery("SELECT AL_ITEM_ID FROM ALL_LISTS WHERE " . 
					"AL_FATHER_ID 	= \'$ParentFolderId\' AND " .
					"AL_DESCRIPTION	= \'$Path\'", 0, 1)) {
					
					if ($QueryResult =~ m/\d+/s) { $FolderId = $& } else { $FolderId = ''}
				}

				# If test folder doesn't yet exist, create it & get the new ID.

				unless ($FolderId) {
					
					if (TestPlanFolderAdd($SubjectPath, $Path) == 0) {	 	
					
						$QueryResult = QCCommonQuery("SELECT AL_ITEM_ID FROM ALL_LISTS WHERE " . 
							"AL_FATHER_ID 	= \'$ParentFolderId\' AND " .
							"AL_DESCRIPTION	= \'$Path\'", 0, 1);
												
						if ($QueryResult =~ m/\d+/s) { $FolderId = $& } else { $FolderId = ''}
						
						if ($FolderId) { print "Results2QC (" . RunTime() ."s):  Added test folder $FolderId - $SubjectPath\\$Path\n" }						
					}
				}
				
				if ($FolderId) {		# Update the SubjectPath and add ID to cache.
				
					if ($SubjectPath) { $SubjectPath .= '\\' }
					$SubjectPath .= $Path;
										
					$FolderIdCache{$SubjectPath} = $FolderId;
				}
				else { last }
			}
		}
		
		return $FolderId;
	}
}

#---------------------------------------------------------------------------------------------------#
# GetTestSetId - Returns the ID of the specified Test Lab test set.
#
# Usage:	GetTestSetId($TestSetPath, $TestSetName)
# Returns:	Test set ID if found or 0, if not.
#
# Notes:	1.	Uses permanent cache of test set IDs, %TestSetIdCache.
#			2.	Will NOT create a new test set; this happens in GetTestInstanceId.
#			3.	Test set folders are stored in the CYCLE_FOLD table, not CYCLE.
# 			4.	qcLabRoot doesn't actually have a 'Root' record in the CYCL_FOLD table.
#			5. 	Stores the TestSetPath in the CY_DESCRIPTION (Config Spec) field to speed queries.
#---------------------------------------------------------------------------------------------------#
{
	my %TestSetIdCache = ();		# Cache of test set IDs.
	
	sub GetTestSetId
	{
		my $TestSetPath = shift;	# Test set parent folder path.
		my $TestSetName = shift;	# Test set name.
	
		my $QueryResult	= '';		# Query result string (CSV format).

		my $TestSetId = 0;			# Test set ID.

		# Check whether folder ID is already in cache.
		
		if (exists $TestSetIdCache{$TestSetPath . '\\' . $TestSetName}) {

			$TestSetId = $TestSetIdCache{$TestSetPath . '\\' . $TestSetName};
		}
		else {
		
			# Query QC to find the test set by TestSetPath & TestSetName.
		
			if ($QueryResult = QCCommonQuery("SELECT CY_CYCLE_ID FROM CYCLE WHERE " . 
				"CY_DESCRIPTION LIKE \'$TestSetPath\' AND " .
				"CY_CYCLE = \'$TestSetName\'", 0, 1)) {
									
				if ($QueryResult =~ m/\d+/s) { $TestSetId = $& } else { $TestSetId = ''}
			}
			
			# If not found, query QC again by folder ID, walking through the folder tree.
					
			unless ($TestSetId) {
		
				my @PathArray = split(/\\/, $TestSetPath);
			
				my $FolderId		= 0;		# Current folder's ID.
				my $ParentFolderId	= 0;		# Parent folder's ID.
				my $RootPath		= '';		# Path of current folder in tree.
				
				# Shift 'Root' off the path array, since QC doesn't actually 
				# have a 'Root' record in the CYCL_FOLD table.
				
				shift @PathArray;
				
				foreach my $Path (@PathArray) {
	
					$ParentFolderId = $FolderId;		# Update the parent folder ID.
				
					# If the test set folder already exists, retrieve it's ID.
				
					if ($QueryResult = QCCommonQuery("SELECT CF_ITEM_ID FROM CYCL_FOLD WHERE " . 
						"CF_FATHER_ID = \'$ParentFolderId\' AND " .
						"CF_ITEM_NAME = \'$Path\'", 0, 1)) {
						
						if ($QueryResult =~ m/\d+/s) { $FolderId = $& } else { $FolderId = ''}
					}
	
					if ($FolderId) {		# If found the test set folder, add it to the cache.
					
						if ($RootPath) { $RootPath .= '\\' }
						$RootPath .= $Path;
					}
					else { last }
				}
				
				# If we found the parent folder, search for the test set.

				if ($FolderId) {
				
					if ($QueryResult = QCCommonQuery("SELECT CY_CYCLE_ID FROM CYCLE WHERE " . 
						"CY_FOLDER_ID = \'$FolderId\' AND " .
						"CY_CYCLE = \'$TestSetName\'", 0, 1)) {
						
						if ($QueryResult =~ m/\d+/s) { $TestSetId = $& } else { $TestSetId = ''}
					}
				}
			}
			
			# If found the test set, add it to the cache.
			
			if ($TestSetId) { $TestSetIdCache{$TestSetPath . '\\' . $TestSetName} = $TestSetId }
		}
		
		return $TestSetId;
	}
}

#---------------------------------------------------------------------------------------------------#
# Transform Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# GetStyleSheet - Return XSLT stylesheet as XML string.
#
# Usage:	GetStyleSheet($SourceXml)
# Returns:	XSLT stylesheet as XML string necessary to provide standardized XPath test suite:
#
#	<testSuite>
#		<test						
#			testName=''					Trim leading -testRoot
#			testDescription=''			Leave Description BLANK
#			testDuration=''				Same as runDuration
#			testAutomation=''			Constant value
#			testProduct=''				Set on cmd line or use default
#			testModule=''				Close match of testName
#			testKeywords=''				Calc from result file type and name
#			testExecutionStatus=''		Use worst result from all steps
#			testStatus=''				Calc from exec status
#			testScriptPath=''			See TestScriptPath
#			testSetPath=''				See TestSetPath
#			testSetNotPassedPath=''		See TestSetNotPassedPath
#			testSetName=''				Parent folder of testName
#		>						
#			<testRun					
#				runName=''				Calc from Release, Suite, Build & Config
#				runBuild=''				Set on cmd line (RN_USER_13 - Build)
#				runConfig=''			Set on cmd line (RN_USER_15 - Environment)
#				runEnvironment=''		Set on cmd line (RN_USER_16 - Environment2)
#				runDuration=''			Same as testDuration (a RUN is for a single test case)
#				runStatus=''			Worst case of all tests
#			/>					
#			<testSteps>					
#				<step				
#					stepName=''			FitNesse: Increment after each assert; Surefire:  Use test name.
#					stepStatus=''		Status is parsed by presence of specific tags (unit) or assert colors (FitNesse).
#				>				
#				<![CDATA[...]]>			Description: FitNesse description is assembled from parsing the result content.  Unit test step descr is blank.
#				</step>				
#				...						Additional <step> elements...
#			</testSteps>					
#		</test>						
#		...								Additional <test> elements...
#	</testSuite>
#
# Notes:	1.	Returns the XSLT stylesheet appropriate for the test results specified (junit, jsunit,
#				or FitNesse, depending upon the content of the results file).
#			2.	If a -stylesheet was specified (or the default, results.xsl, exists), it will use 
#				that stylesheet file.
#			3.  Stylesheets may contain argument variables, such as {{build}}, which will be replaced
#				with the corresponding value of $Args{}.
#
#---------------------------------------------------------------------------------------------------#
sub GetStyleSheet
{
	my $SourceXML	= shift;		# Original test suite source XML as XPath object.
	
	my $ResultType	= 'junit';
	my $StyleSheet	= '';			# Appropriate stylesheet as XML string.
	
	# Determine the result type (default is junit).
	
	if ($SourceXML =~ m/jsunit/) 				{ $ResultType = 'jsunit' }
	if ($SourceXML =~ m/<relativePageName>/)	{ $ResultType = 'FitNesse' }
	
	unless ($Args{testTool}) { $Args{testTool} = "TestTool=$ResultType"}
	
	my %Xsl = (
			
		# XSLT stylesheet for FitNesse test results.
		
		'FitNesse' =>
		'<?xml version="1.0" encoding="UTF-8"?>' .
		'<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">' .
		'<xsl:template match="/">' .
			'<testSuite>' .
			'<xsl:for-each select="testResults/result">' .
				'<test>' . 
			
					# Add test case attributes.
			
					'<xsl:attribute name="testName"><xsl:value-of select="relativePageName"/></xsl:attribute>' .
					'<xsl:attribute name="testDescription"/>' .
					'<xsl:variable  name="testDuration">0</xsl:variable>' .
					'<xsl:attribute name="testDuration"><xsl:value-of select="$testDuration"/></xsl:attribute>' .
					'<xsl:attribute name="testAutomation">Automated</xsl:attribute>' .
					'<xsl:attribute name="testProduct">'  . $Args{product}  . '</xsl:attribute>' .
					'<xsl:attribute name="testModule">'   . $Args{module}   . '</xsl:attribute>' .
					'<xsl:attribute name="testKeywords">' . $Args{testTool} . ' ' . $Args{keywords} . '</xsl:attribute>' .
					
					# The testScriptPath, testSetPath and testSetName are first constructed as
					# a concatenation of (qcLabRoot || qcPlanSubject), classpath & name, then
					# any specified testRoot part of the path is removed in later processing.
						
					'<xsl:attribute name="testScriptPath">' . $Args{qcPlanSubject} .
						'.<xsl:value-of select="relativePageName"/>' .
					'</xsl:attribute>' .
					'<xsl:attribute name="testSetPath">' . $Args{qcLabRoot} .
						'.<xsl:value-of select="relativePageName"/>' .
					'</xsl:attribute>' .
					'<xsl:attribute name="testSetNotPassedPath">' . $Args{qcLabRootNotPassed} .
						'.<xsl:value-of select="relativePageName"/>' .
					'</xsl:attribute>' .
					'<xsl:attribute name="testSetName">' . 
						'.<xsl:value-of select="relativePageName"/>' .
					'</xsl:attribute>' .
					
					# Check the <counts/> values to set the overall testStatus and RunStatus.
					# Default is "No Run" and "Repair".
                    # Used to have "Review" test status for No Run and Failed run statuses.
											
					'<xsl:choose>' .
						'<xsl:when test="counts/exceptions > 0">' .
							'<xsl:attribute name="testExecutionStatus">Not Completed</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Repair</xsl:attribute>' .
							'</xsl:when>' .			
						'<xsl:when test="counts/wrong > 0">' .
							'<xsl:attribute name="testExecutionStatus">Failed</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Repair</xsl:attribute>' .
							'</xsl:when>' .
						'<xsl:when test="counts/right > 0">' .
							'<xsl:attribute name="testExecutionStatus">Passed</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Ready</xsl:attribute>' .
							'</xsl:when>' .						
						'<xsl:otherwise>' .
							'<xsl:attribute name="testExecutionStatus">No Run</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Repair</xsl:attribute>' .
							'</xsl:otherwise>' .
					'</xsl:choose>' .

					# Add testRun attributes.
				
					'<testRun>' . 
						'<xsl:attribute name="runName">' .
							$Args{release} . ' '    . $Args{suite}  . ' for '. 
							$Args{build}   . ' on ' . $Args{config} .
						'</xsl:attribute>' .
						'<xsl:attribute name="runBuild">'		. $Args{build}		 . '</xsl:attribute>' .
						'<xsl:attribute name="runConfig">'		. $Args{config}		 . '</xsl:attribute>' .
						'<xsl:attribute name="runEnvironment">'	. $Args{environment} . '</xsl:attribute>' .
						'<xsl:attribute name="runDuration"><xsl:value-of select="$testDuration"/></xsl:attribute>' .
						
						'<xsl:choose>' .
							'<xsl:when test="counts/exceptions > 0">' .
								'<xsl:attribute name="runStatus">Not Completed</xsl:attribute>' .
								'</xsl:when>' .							
							'<xsl:when test="counts/wrong > 0">' .
								'<xsl:attribute name="runStatus">Failed</xsl:attribute>' .
								'</xsl:when>' .
							'<xsl:when test="counts/right > 0">' .
								'<xsl:attribute  name="runStatus">Passed</xsl:attribute>' .
								'</xsl:when>' .
							'<xsl:otherwise>' .
								'<xsl:attribute  name="runStatus">No Run</xsl:attribute>' .
								'</xsl:otherwise>' .
						'</xsl:choose>' .
					'</testRun>' .
													
					# Add test steps.  The step Description will be blank unless the step did not pass.
				
					'<testSteps>' .
						'<step>' .
							'<xsl:attribute name="stepName">' .
								'<xsl:value-of select="relativePageName"/></xsl:attribute>' .							
							'<xsl:choose>' .
								'<xsl:when test="counts/exceptions > 0">' .
									'<xsl:attribute name="stepStatus">Not Completed</xsl:attribute>' .
									'</xsl:when>' .							
								'<xsl:when test="counts/wrong > 0">' .
									'<xsl:attribute name="stepStatus">Failed</xsl:attribute>' .
									'</xsl:when>' .
								'<xsl:when test="counts/right > 0">' .
									'<xsl:attribute  name="stepStatus">Passed</xsl:attribute>' .
									'</xsl:when>' .
								'<xsl:otherwise>' .
									'<xsl:attribute  name="stepStatus">No Run</xsl:attribute>' .
									'</xsl:otherwise>' .
							'</xsl:choose>' .
							'<xsl:value-of select="content"/>' .
						'</step>' .
					'</testSteps>' .
						
				'</test>' .	
			'</xsl:for-each>' .
			'</testSuite>' .
		'</xsl:template>' .
		'</xsl:stylesheet>',
		
		# XSLT stylesheet for junit test results.
		
		'junit' =>
		'<?xml version="1.0" encoding="UTF-8"?>' .
		'<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">' .
		'<xsl:template match="/">' .
			'<testSuite>' .
			'<xsl:for-each select="testsuite/testcase">' .
				'<test>' . 
			
					# Add test case attributes.
			
					'<xsl:attribute name="testName"><xsl:value-of select="@name"/></xsl:attribute>' .
					'<xsl:attribute name="testDescription"/>' .
					'<xsl:attribute name="testDuration"><xsl:value-of select="ceiling(@time)"/></xsl:attribute>' .
					'<xsl:attribute name="testAutomation">Automated</xsl:attribute>' .
					'<xsl:attribute name="testProduct">'  . $Args{product}  . '</xsl:attribute>' .
					'<xsl:attribute name="testModule">'   . $Args{module}   . '</xsl:attribute>' .
					'<xsl:attribute name="testKeywords">' . $Args{testTool} . ' ' . $Args{keywords} . '</xsl:attribute>' .
					
					# The testScriptPath, testSetPath and testSetName are first constructed as
					# a concatenation of (qcLabRoot || qcPlanSubject), classpath & name, then
					# any specified testRoot part of the path is removed in later processing.
						
					'<xsl:attribute name="testScriptPath">' . $Args{qcPlanSubject} .
						'.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/>' .
					'</xsl:attribute>' .
					'<xsl:attribute name="testSetPath">' . $Args{qcLabRoot} .
						'.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/>' .
					'</xsl:attribute>' .
					'<xsl:attribute name="testSetPath">' . $Args{qcLabRootNotPassed} .
						'.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/>' .
					'</xsl:attribute>' .
					'<xsl:attribute name="testSetName">' . 
						'.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/>' .
					'</xsl:attribute>' .
					
					# Since test= gives an error if an element (like 'failure') doesn't exist,
					# we use xsl:for-each instead of xsl:choose, overwriting the default values
					# of 'Passed' and 'Ready' if a skipped, failure or error element exists.

					'<xsl:choose>' .
						'<xsl:when test="error">' .
							'<xsl:attribute name="testExecutionStatus">Not Completed</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Repair</xsl:attribute>' .
							'</xsl:when>' .
						'<xsl:when test="failure">' .
							'<xsl:attribute name="testExecutionStatus">Failed</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Repair</xsl:attribute>' .
							'</xsl:when>' .					
						'<xsl:when test="skipped">' .
							'<xsl:attribute name="testExecutionStatus">No Run</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Repair</xsl:attribute>' .
							'</xsl:when>' .				
						'<xsl:otherwise>' .
							'<xsl:attribute name="testExecutionStatus">Passed</xsl:attribute>' .
							'<xsl:attribute name="testStatus">Ready</xsl:attribute>' .
							'</xsl:otherwise>' .
					'</xsl:choose>' .
						
					# Add testRun attributes.
				
					'<testRun>' . 
						'<xsl:attribute name="runName">' .
							$Args{release} . ' '    . $Args{suite}  . ' for '. 
							$Args{build}   . ' on ' . $Args{config} .
						'</xsl:attribute>' .
						'<xsl:attribute name="runBuild">'		. $Args{build}		 . '</xsl:attribute>' .
						'<xsl:attribute name="runConfig">'		. $Args{config}		 . '</xsl:attribute>' .
						'<xsl:attribute name="runEnvironment">'	. $Args{environment} . '</xsl:attribute>' .
						'<xsl:attribute name="runDuration"><xsl:value-of select="@time"/></xsl:attribute>' .

						'<xsl:choose>' .
							'<xsl:when test="error">' .
								'<xsl:attribute name="runStatus">Not Completed</xsl:attribute>' .
								'</xsl:when>' .
							'<xsl:when test="failure">' .
								'<xsl:attribute name="runStatus">Failed</xsl:attribute>' .
								'</xsl:when>' .					
							'<xsl:when test="skipped">' .
								'<xsl:attribute name="runStatus">No Run</xsl:attribute>' .
								'</xsl:when>' .				
							'<xsl:otherwise>' .
								'<xsl:attribute name="runStatus">Passed</xsl:attribute>' .
								'</xsl:otherwise>' .
						'</xsl:choose>' .
					'</testRun>' .
													
					# Add test steps.  The step Description will be blank unless the step did not pass.
				
					'<testSteps>' .
						'<step>' .
							'<xsl:attribute name="stepName"><xsl:value-of select="@name"/></xsl:attribute>' .

							'<xsl:choose>' .
								'<xsl:when test="error">' .
									'<xsl:attribute name="stepStatus">Not Completed</xsl:attribute>' .
									'ERROR - <xsl:value-of select="@message"/>' .
									'</xsl:when>' .
								'<xsl:when test="system-err">' .
									'<xsl:attribute name="stepStatus">Failed</xsl:attribute>' .
									'FAILED - System error:  <xsl:value-of select="."/>' .
									'</xsl:when>' .
								'<xsl:when test="failure">' .
									'<xsl:attribute name="stepStatus">Failed</xsl:attribute>' .
									'FAILED - Failure message:  <xsl:value-of select="@message"/>' .
									'<xsl:value-of select="."/>' .
									'</xsl:when>' .
								'<xsl:when test="skipped">' .
									'<xsl:attribute name="stepStatus">No Run</xsl:attribute>' .
									'</xsl:when>' .
								'<xsl:otherwise>' .
									'<xsl:attribute name="stepStatus">Passed</xsl:attribute>' .
									'</xsl:otherwise>' .
							'</xsl:choose>' .
													
						'</step>' .
					'</testSteps>' .
						
				'</test>' .	
			'</xsl:for-each>' .
			'</testSuite>' .
		'</xsl:template>' .
		'</xsl:stylesheet>',
	);
	
	$Xsl{'jsunit'} = $Xsl{'junit'};			# Stylesheet for jsunit is the same as for junit.

	# If there is an external XSL file available, use it.  Replace any {{variables}} specified in 
	# the stylesheet with the actual $Arg{} values.
	
	if (-e $Args{stylesheet}) {
		
		my $Xslt	= XML::LibXSLT->new();
		$StyleSheet	= XML::LibXML->load_xml(location => $Args{stylesheet})->toString;

		foreach my $ThisArg (keys %Args) {

			$StyleSheet =~ s/{{$ThisArg}}/$Args{$ThisArg}/g;
		}
	}
	
	# Otherwise, use the default stylesheet, based upon the result type.
	
	else { $StyleSheet = $Xsl{$ResultType} }

	return $StyleSheet;
}

#---------------------------------------------------------------------------------------------------#
# XformTestSuite - Transform a test suite from various XML formats to standard XPath format.
#
# Usage:	XformTestResults($XmlFile)
# Returns:	Test suite as a standardized XPath object.
#---------------------------------------------------------------------------------------------------#
sub XformTestSuite
{
	my $XmlFile	= shift;	# Original test suite filename.
		
	# Create a new XSLT object and load the source XML.  If there's a parsing issue, return an empty Xpath object.

	my $Xslt	= XML::LibXSLT->new();	
	my $source 	= XML::LibXML->new();

	eval {
	
		$source = XML::LibXML->load_xml(location => $XmlFile);
	
	} or do {

		return XML::XPath::XMLParser->new();
	};	

	# Setting no_cdata in load_xml() doesn't actually omit the CDATA content, just the CDATA tags.  
	# So, we leave the tags and remove any CDATA using regex.

	$source = $source->toString();
	
	$source =~ s/<!\[CDATA\[.*?\]\]>//sg;					# This will remove any CDATA from the XML (such as with FitNesse).
	$source = XML::LibXML->load_xml(string => $source);

	# Fetch the appropriate stylesheet based upon the format of the source XML.

	my $style_doc 	= XML::LibXML->load_xml(string => GetStyleSheet($source->toString()), no_cdata => 1);
	my $stylesheet 	= $Xslt->parse_stylesheet($style_doc);	

	# Transform the source XML using the stylesheet, and save it as an XML string.

	my $results 	= $stylesheet->transform($source);
	my $XformedXml	= $stylesheet->output_as_bytes($results);

	# Perform any post-translation processing that we couldn't do easily with XSLT.
	
	# Remove any folders from testName, stepName & testSetName.

    $XformedXml =~ s/testName=".*?([0-9A-Za-z_\s\(\)\-]+)"/testName="$1"/g;
    $XformedXml =~ s/stepName=".*?([0-9A-Za-z_\s\(\)\-]+)"/stepName="$1"/g;

    $XformedXml =~ s/testSetName=".*?\.([0-9A-Za-z_\s\(\)\-]+)\.[0-9A-Za-z_\s\(\)\-]+"/testSetName="$1"/g;	
    $XformedXml =~ s/testSetName="\.+/testSetName="/g;
	
	# Remove testRoot (if spec'd) and replace '.' in path with '\'.

	if (exists $Args{testRoot}) { $XformedXml =~ s/$Args{testRoot}//g }
	
	while ($XformedXml =~ /Path=".+?\.+\D.+?"/) {
	
		my $BeforeStr	= $`;
		my $PathStr		= $&;
		my $AfterStr	= $';
	
		$PathStr    =~ s/\.+(\D)/\\$1/g;
		$XformedXml = $BeforeStr . $PathStr . $AfterStr;
	}

	# Remove testName from testScriptPath and remove testSetName\testName from testSetPath.

    $XformedXml =~ s/testScriptPath="(.+?)\\[0-9A-Za-z_\s\(\)\-]+"/testScriptPath="$1"/g;	
    $XformedXml =~ s/testSetPath="(.+?)\\[0-9A-Za-z_\s\(\)\-]+\\[0-9A-Za-z_\s\(\)\-]+"/testSetPath="$1"/g;	
    $XformedXml =~ s/testSetNotPassedPath="(.+?)\\[0-9A-Za-z_\s\(\)\-]+\\[0-9A-Za-z_\s\(\)\-]+"/testSetNotPassedPath="$1"/g;	

    # Avoid tokenization of "&" to &amp; - for some reason this causes the soap->call() XML parser to fail.
    
    $XformedXml =~ s/&/_amp_/g;        

	# Convert the final XML to an XPath object.
	
  	my $TestSuite  = XML::XPath::XMLParser->new(xml => $XformedXml)->parse;

	return $TestSuite;
}

#---------------------------------------------------------------------------------------------------#
# Utility Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# LogStatus - Log success/fail status to STDOUT.
#
# Usage:	LogStatus($MethodName, $MethodResult, $StartTime [, $QueryString])
# Returns:	Nil.
#---------------------------------------------------------------------------------------------------#
sub LogStatus
{
	my $MethodName 	 = shift;		# Method name
	my $MethodResult = shift;		# SOAP result hash
	my $StartTime    = shift;		# Time prior to calling MethodName
	my $QueryString	 = shift;		# Query string (optional)

	my $Duration = sprintf '%.2f', (gettimeofday() - $StartTime);
	
	my $SuccessFlag 		= $MethodResult->valueof("//${MethodName}Result/SuccessFlag");
	my $ActualReturnValue 	= $MethodResult->valueof("//${MethodName}Result/ActualReturnValue");
	my $ErrorDescription 	= $MethodResult->valueof("//${MethodName}Result/ErrorDescription");

	if ($SuccessFlag == 0)
	{
		if ($LogLevel > 1) {
			print "Results2QC (" . RunTime() ."s):    $MethodName succeeded ($Duration secs)", ;
			print ", returning '$ActualReturnValue'" if $ActualReturnValue;
			print " - $QueryString" if $QueryString;
			print "\n";	
		}
	} 
	elsif ($LogLevel)
	{
		print "Results2QC (" . RunTime() ."s):    $MethodName FAILED ($Duration secs)", ;
		print ", returning '$ErrorDescription'" if $ErrorDescription;
		print " - $QueryString" if $QueryString;
		print "\n";
	}	
}

#---------------------------------------------------------------------------------------------------#
# RunTime - Elapsed run time, in seconds.
#
# Usage:	RunTime()
# Returns:	Elapsed run time, in seconds.
#---------------------------------------------------------------------------------------------------#
{
	my $StartTime = 0;

	sub RunTime
	{
		unless ($StartTime) { $StartTime = gettimeofday() }
	
		return int(gettimeofday() - $StartTime);
	}
}


#---------------------------------------------------------------------------------------------------#
# ProcessFiles - Transform and import one of more results files.
#
# Usage:	ProcessFiles($resultFiles)
# Returns:	
#---------------------------------------------------------------------------------------------------#
sub ProcessFiles
{
	my $ResultFiles = shift;	# Fully-qualified filenames.  Includes path, may include wildcards.

	while (glob($ResultFiles)) {

		if (-e $_) {

			print "Results2QC (" . RunTime() ."s):  Processing results file $_\n";

			my $TestSuite = XformTestSuite($_);

			# For each test case in the suite, add the test run, run steps and
			# a test instance/test case, if not already present.  Use eval{} in case there's a problem.

			eval {
			
				foreach my $ThisTest ($TestSuite->find('/testSuite/test')->get_nodelist) {

					my $StartTime		= gettimeofday();

					AddTestRun($ThisTest);

					# End session & re-connect if adding the test run required > 60 secs.

					if ((gettimeofday() - $StartTime) > 60) {
					
						QCCommonDisconnect();
						$strClientId = QCCommonConnect();
					}
				}
			}
		}
	}	
}
	
#---------------------------------------------------------------------------------------------------#
# SearchFolders - Search the SearchPath for matching folders and process any files within them.
#
# Usage:	SearchFolders($searchPath, $searchFolders)
# Returns:	
#---------------------------------------------------------------------------------------------------#
sub SearchFolders
{
	my $SearchPath 		= shift;				# Path in which to begin folder search.
	my $SearchFolders 	= shift;				# Regex of specific folder name to search.
	
	my $ResultFiles 	= $Args{resultFiles};	# File(s) to process.
	
	# Process any results files in the current folder if it matches the searchFolders name
	# or SearchFolders is blank.
	
	if ($SearchPath) { 
		
		$SearchPath =~ s|\\|/|g;			# Swap '\' to '/' in path.
		$ResultFiles = $SearchPath . '/' . $ResultFiles;
	}

	if ((! $SearchPath) || (! $SearchFolders) || ($SearchPath =~ m/\/$SearchFolders$/)) { 
	
		$ResultFiles =~ s|(.)//|$1/|g;		# Remove any '//' (except a leading '//').

		ProcessFiles($ResultFiles);			# Process files in the current folder.	
	}
	
	# If SearchFolders is specified, search for any matching sub-folders.
	
	unless ($SearchPath) { $SearchPath = '.' }
	
	if (($SearchFolders) || (-d $SearchPath)) {	
	
		opendir(DIR, $SearchPath);
	
		foreach my $CurrentFolder (readdir(DIR)) {

			unless ($CurrentFolder =~ m/\.+/) {
				
				$CurrentFolder = $SearchPath . '/' . $CurrentFolder;

				if (-d $CurrentFolder) { SearchFolders($CurrentFolder, $SearchFolders) }
			}
		}
	}	
}
	
#---------------------------------------------------------------------------------------------------#
# SetArgs - Return a hash of validated command-line arguments.
#
# Usage:	SetArgs(\%Args)
# Returns:	Hash of valid arguments parsed from command line or nil if a required arg is missing.
#---------------------------------------------------------------------------------------------------#
sub SetArgs
{
	my $Args = shift;				# Reference to %Args hash.
	
	my $AllFilesOk = 1;				# Flag to check whether all specified files exist.

	my %CorrectArg = (
		'-BUILD' 			=> 'build',
		'-CONFIG'			=> 'config',		
		'-ENVIRONMENT' 	 	=> 'environment',
		'-HTTPPROXY'		=> 'httpProxy',
		'-KEYWORDS'			=> 'keywords',
		'-MODULE'  			=> 'module',
		'-PRODUCT'  		=> 'product',
		'-QCLABROOT'  		=> 'qcLabRoot',
		'-QCLABROOTNOTPASSED'  => 'qcLabRootNotPassed',
		'-QCPLANSUBJECT'  	=> 'qcPlanSubject',
		'-QCPROXY'		    => 'qcProxy',
		'-RELEASE'  		=> 'release',
		'-RESULTFILES' 		=> 'resultFiles',
		'-SEARCHFOLDERS'	=> 'searchFolders',
		'-SEARCHPATH' 		=> 'searchPath',
		'-SMLOGFILE' 		=> 'smLogFile',		
		'-STYLESHEET' 		=> 'stylesheet',
		'-SUITE'  			=> 'suite',
		'-TESTGROUP'  		=> 'testGroup',
		'-TESTROOT'  		=> 'testRoot',
		'-TESTTOOL'  		=> 'testTool',
		'-UPDATESTEPS' 		=> 'updateSteps',
	);
	
	my $DefaultResultFile	= 'results.xml';
	my $DefaultSmLogFile 	= '..\\..\\build\\server-dist\\logs\\sm.log';
	my $DefaultStylesheet 	= 'results.xsl';
	
	# Process each argument on the command line.
	
	foreach my $ThisArg (keys %$Args) {

		if (exists $CorrectArg{uc $ThisArg}) {
			
			$Args->{$CorrectArg{uc $ThisArg}} = $Args->{$ThisArg};
		}
		else {
			
			print ">>> WARNING - Ignoring unknown argument: '$ThisArg'\n";
		}
		
		delete $Args->{$ThisArg}; 
	}

	# Check for missing required arguments.

	my $MissingArg = 0;

	unless ($Args->{release}) { 
		$MissingArg = print "\n>>> ERROR - Must specify argument: '-release'";
	}
	unless ($Args->{suite}) {
		$MissingArg = print "\n>>> ERROR - Must specify argument: '-suite'";
	}
	
	if ($MissingArg) { return }

	# Set default values for any non-required arguments not already specified.

	unless ($Args->{build}) {
		$Args->{build} = '';
	}
	unless ($Args->{config}) {
		$Args->{config} = '';
	}
	unless ($Args->{environment}) {
		$Args->{environment} = '';
	}
	unless ($Args->{httpProxy}) {
		$Args->{httpProxy} = 'http://web-proxy.corp.hp.com:8088';		# Default proxy for HP AMERICAS domain.
	}
	unless ($Args->{keywords}) {
		$Args->{keywords} = '';
	}
	unless ($Args->{module}) {
		$Args->{module} = '';
	}
	unless ($Args->{product}) {
		$Args->{product} = 'Service Manager';
	}
	unless ($Args->{qcLabRoot}) {
		$Args->{qcLabRoot} = "Root\\$Args->{product}\\$Args->{release}\\AutoTests\\$Args->{suite}";
	}
	unless ($Args->{qcLabRootNotPassed}) {
		$Args->{qcLabRootNotPassed} = $Args->{qcLabRoot};
	}
	unless ($Args->{qcPlanSubject}) {
		$Args->{qcPlanSubject} = "Subject\\$Args->{product}\\$Args->{release}\\AutoTests";
	}
	unless ($Args->{qcProxy}) {
		$Args->{qcProxy} = 'http://g6w2381g.atlanta.hp.com/qcproxy/wsqccommon.asmx';		# Default QC Proxy server.
	}
	unless ($Args->{resultFiles}) {
		$Args->{resultFiles} = $DefaultResultFile;
	}
	unless ($Args->{searchFolders}) {
		$Args->{searchFolders} = '';
	}
	unless ($Args->{searchPath}) {
		$Args->{searchPath} = '';
	}
	unless ($Args->{smLogFile}) {
		$Args->{smLogFile} = $DefaultSmLogFile;
	}
	unless ($Args->{stylesheet}) {
		$Args->{stylesheet} = $DefaultStylesheet;
	}
	unless ($Args->{testTool}) {
		$Args->{testTool} = '';
	}
	unless ($Args->{testGroup}) {
		$Args->{testGroup} = 'SWP HPC US Client Scrum';
	}
	
	# If -updateSteps was specified, set it to a reasonable value.
	
	$Args->{updateSteps} = 'true' if (exists $Args->{updateSteps}); 
	
	# Check whether all the resultFiles exist.
	
	while (glob($Args->{resultFiles})) {

		unless (-e $_) {

			print "\n>>> ERROR - Result file '$_' does not exist.";
			$AllFilesOk = 0;
		}
	}
	
	# Check whether the stylesheet XSL file exists (unless using the default, which doesn't have to).
	
	unless ($Args->{stylesheet} eq $DefaultStylesheet) {
				
		unless (-e $Args->{stylesheet}) {

			print "\n>>> ERROR - Stylesheet file '$Args->{stylesheet}' does not exist.";
			$AllFilesOk = 0;
		}
	}

	# If no -build was specified, try to determine the SM build number by searching the smLogFile.
	
	unless ($Args->{build}) {
				
		if (-e $Args->{smLogFile}) {

			open (my $smLogFile, '<', $Args->{smLogFile});
			
			my $build = '';
			
			while (<$smLogFile>) {

				$build = $_;
	
				if ( $build =~ m/RTE I Process sm.+?\(-(.+?)\)/ ) {
					$Args->{build} = $1;
					last;
				}
			}			
		}
		elsif ($Args->{smLogFile} ne $DefaultSmLogFile) {
			print "\n>>> Warning - Couldn't find SM Log file '$Args->{smLogFile}'.";
		}
		
		# smLogFile is not required; if the build # could not be determined, set it to blank.
		
		unless ($Args->{build}) { $Args->{build} = '' }		
	}
	
	unless ($AllFilesOk) { return };

	return %$Args;
}

#---------------------------------------------------------------------------------------------------#
# ShowUsage - Show the USAGE message.
#
# Usage:	ShowUsage()
# Returns:	Nil.
#---------------------------------------------------------------------------------------------------#
sub ShowUsage
{
	print "\n\nUSAGE:     perl Results2QC.pl -release {release} -suite {suite}\n" .
		"\t\t[-resultFiles {filename(s)}] [-stylesheet {xsl file}]\n" .
		"\t\t[-searchPath {path}] [-searchFolders {folder}]\n" .
		"\t\t[-product {product name}] [-build {build number}]\n". 
		"\t\t[-config {configuration}] [-environment {environment}]\n" .
		"\t\t[-module {module}] [-keywords {keywords}] [-testTool {test tool}]\n" .
		"\t\t[-httpProxy {httpProxy}] [-qcProxy {qcProxy}] [-testRoot {test root}]\n" .
		"\t\t[-qcPlanSubject {Test Plan folder}] [-qcLabRoot {Test Lab folder}]\n" .
		"\t\t[-qcLabRootNotPassed {Test Lab folder}]\n" .
		"\t\t[-smLogFile {SM log file}] [-updateSteps true]\n\n" .
	"\t\tRequired arguments:\n\n" .
		"\t\t-release\tProduct release name.\n" .
		"\t\t-suite\t\tTest suite (test set) name.\n" .
	"\n\t\tOptional arguments:\n\n" .
		"\t\t-resultFiles\tList of one or more result files, in quotes (default = results.xml).\n" .
		"\t\t-stylesheet\tOptional custom stylesheet (default = results.xsl, if it exists).\n" .
		"\t\t-searchPath\tPath to search for result files (default = current dir).\n" .
		"\t\t-searchFolders\tIf specified, will search for result files in matching subfolders.\n\n" .
		"\t\t-product\tProduct name (default = 'Service Manager').\n" .
		"\t\t-build\t\tBuild number or name (also see -smLogFile).\n" .
		"\t\t-config\t\tTest configuration name (Environment field).\n" .
		"\t\t-environment\tTest environment values (Environment 2 field).\n\n" .
		"\t\t-module\t\tTest module.\n" . 
		"\t\t-keywords\tTest keywords (automatically includes -testTool).\n" .
		"\t\t-testTool\tTest tool name (default is determined by format of results file).\n\n" .
		"\t\t-httpProxy\tHTTP proxy (default = 'http://16.213.0.40:8080').\n" .
		"\t\t-qcProxy\tQC Proxy server (default = 'http://g6w1841g.atlanta.hp.com/qcproxy/wsqccommon.asmx').\n" .
		"\t\t-testRoot\tRoot path of actual test cases (omitted from test paths).\n" .
		"\t\t-qcPlanSubject\tTest Plan folder in QC (default = 'Subject\\{product}\\{release}\\AutoTests').\n" .
		"\t\t-qcLabRoot\tTest Lab folder in QC (default = 'Root\\{product}\\{release}\\AutoTests\\{suite}').\n" .
		"\t\t-qcLabRootNotPassed\tTest Lab folder in QC for non-passing tests.\n" .
		"\t\t-smLogFile\tPath to sm.log file (used to determine build, if no -build specified).\n\n" .
		"\t\t-updateSteps\tUpdate all test case design steps.\n\n" .
	"EXAMPLE:   perl Results2QC.pl -release 'SM 9.xx' -suite 'Weekly BAT'\n" .
					"\t\t-resultFiles 'TEST*.xml' -build 'DAILY.123' -config '02_AIX_DB2_WAS'\n" .
					"\t\t-testRoot 'Service Manager.AutoTests.'\n\n" .

	return;
}

#---------------------------------------------------------------------------------------------------#
# QCProxy - QCCommon Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# QCCommonConnect - Login to QC proxy.
#
# Usage:	QCCommonConnect()
# Returns:	strClientId (e.g., 'http://qc.houston.hp.com/qcbin/|BTO|ITA|_svc_SM_Automation|6912')
#
# Notes:	Uses hard-coded values for QC Server, Domain, Project, User & Password, 
#---------------------------------------------------------------------------------------------------#
sub QCCommonConnect
{
	my $strMethod = 'QCCommonConnect';
	
	# These are the values for the Service Manager QC server, project and service account.
	
	my $strQcServerName = 'http://qc1d.atlanta.hp.com/qcbin/';
	my $strQcDomain 	= 'IPG';
	my $strQcProject 	= 'LifeSaver';
	# my $strQcProject 	= 'cloudservice';

	# my $strQcDomain 	= 'IPG_SIRIUS';
	# my $strQcProject 	= 'SIRIUS_FW';

	my $strQcUser 		= '_svc_r2qc';
	my $strQcPassword 	= 'CZm36Ymm1dC8NTIy1lxcCeXUbRNM5MtJjfehIJVFMyNw06RI3irxSDlNnDmT/NcgesCks/vg26xOjTJCzmpNF8e66IsmoKV1b7FaYUGqiN/mGmNFW1tsqYBjVVgj5NRO9dUKTsrU8wCNEG/x6dFKLlNfdhBeaXA63lFKYB3QUQ4=';
	
	my $start = gettimeofday();
	
	my $result = $soap->call(
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
		SOAP::Data->name('strQcUser')->value($strQcUser),
		SOAP::Data->name('byteQcPassword')->value($strQcPassword),
		SOAP::Data->name('strQcDomain')->value($strQcDomain),
		SOAP::Data->name('strQcProject')->value($strQcProject),
		SOAP::Data->name('strServerName')->value($strQcServerName), ));
		
	my $PrevLogLevel = $LogLevel;
	$LogLevel = 2;											# Always report connect/disonnect status.
		
	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	$LogLevel = $PrevLogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# QCCommonDisconnect - Disconnect from QC proxy.
#
# Usage:	QCCommonDisconnect()
# Returns:	0 if successful
#---------------------------------------------------------------------------------------------------#
sub QCCommonDisconnect
{
	my $strMethod = 'QCCommonDisconnect';
	
	my $start = gettimeofday();
	
	my $result = $soap->call( 
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
    	SOAP::Data->name('strClientId')->value($strClientId), ));
	               
	my $PrevLogLevel = $LogLevel;
	$LogLevel = 2;											# Always report connect/disonnect status.

	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	$LogLevel = $PrevLogLevel;

	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# QCCommonUserEmailGet - Return email address for specified QC user name.
#
# Usage:	QCCommonUserEmailGet($strQcUserName)
# Returns:	User email address (e.g., 'david.c.cooper@hp.com')
#---------------------------------------------------------------------------------------------------#
sub QCCommonUserEmailGet
{
	my $strQcUserName = shift;		# QC User ID (e.g., david.c.cooper_hp.com)
	
	my $strMethod = 'QCCommonUserEmailGet';
	
	my $start = gettimeofday();
	
	my $result = $soap->call(
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
		SOAP::Data->name('strClientId')->value($strClientId),
		SOAP::Data->name('strQcUserName')->value($strQcUserName), ));

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# QCCommonDomainGet - Return QC domain name for the current session.
#
# Usage:	QCCommonDomainGet()
# Returns:	QC domain name (e.g., 'BTO')
#---------------------------------------------------------------------------------------------------#
sub QCCommonDomainGet
{
	my $strMethod = 'QCCommonDomainGet';
	
	my $start = gettimeofday();
	
	my $result = $soap->call(
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
		SOAP::Data->name('strClientId')->value($strClientId), ));

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}
  
#---------------------------------------------------------------------------------------------------#
# QCCommonProjectGet - Return QC project name for the current session.
#
# Usage:	QCCommonProjectGet()
# Returns:	QC project name (e.g., 'ITA')
#---------------------------------------------------------------------------------------------------#
sub QCCommonProjectGet
{
	my $strMethod = 'QCCommonProjectGet';
	
	my $start = gettimeofday();
	
	my $result = $soap->call(
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
		SOAP::Data->name('strClientId')->value($strClientId), ));

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}
  
#---------------------------------------------------------------------------------------------------#
# QCCommonListAdd - Add an item to a list in QC.
#
# Usage:	QCCommonListAdd($strListName, $strListItem)
# Returns:	0 if successful
#
# Notes:	Does not support multi-level lists (like 'ListBranch\Item 01')
#---------------------------------------------------------------------------------------------------#
sub QCCommonListAdd
{
	my $strListName = shift;		# QC list name (e.g., 'Test_Env 2')
	my $strListItem = shift;		# List item to add (e.g. 'New Item")
	
	my $strMethod = 'QCCommonListAdd';
	
	my $start = gettimeofday();
	
	my $result = $soap->call(
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
		SOAP::Data->name('strClientId')->value($strClientId),
		SOAP::Data->name('strListName')->value($strListName),
		SOAP::Data->name('strListItem')->value($strListItem), ));
	
   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# QCCommonListRemove - Remove an item from a list in QC.
#
# Usage:	QCCommonListRemove($strListName, $strListItem)
# Returns:	0 if successful
#
# Notes:	Does not support multi-level lists (like 'ListBranch\Item 01')
#---------------------------------------------------------------------------------------------------#
sub QCCommonListRemove
{
	my $strListName = shift;		# QC list name (e.g., 'Test_Env 2')
	my $strListItem = shift;		# List item to remove (e.g. 'New Item")
	
	my $strMethod = 'QCCommonListRemove';
	
	my $start = gettimeofday();
	
	my $result = $soap->call(
		SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
		SOAP::Data->name('strClientId')->value($strClientId),
		SOAP::Data->name('strListName')->value($strListName),
		SOAP::Data->name('strListItem')->value($strListItem), ));
	
   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# QCCommonQuery - Generic quesry from QC.
#
# Usage:	QCCommonQuery($strQuery, $intMinIndex, $strMaxIndex)
# Returns:	CSV string containing results of SQL query.
#
# Example:	QCCommonQuery("SELECT TS_TEST_ID, TS_EXEC_STATUS, TS_NAME FROM TEST " . 
#				"WHERE TS_USER_12 = \'Service Manager\' AND TS_USER_05 = \'Automated\' " .
#				"ORDER BY TS_TEST_ID", 1, 10);
#
# Notes:	Queries can occasionally timeout, so we retry.
#---------------------------------------------------------------------------------------------------#
sub QCCommonQuery
{
	my $strQuery 	= shift;		# SQL query statement (e.g., 'SELECT * FROM TEST')
	my $intMinIndex = shift;		# First row to report (0 for all rows)
	my $intMaxIndex = shift;		# Last row to report (0 for all rows)
	
	my $strMethod = 'QCCommonQuery';
	
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strQuery')->value($strQuery),
			SOAP::Data->name('intMinIndex')->value($intMinIndex),
			SOAP::Data->name('intMaxIndex')->value($intMaxIndex), ));
	};

	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strQuery')->value($strQuery),
				SOAP::Data->name('intMinIndex')->value($intMinIndex),
				SOAP::Data->name('intMaxIndex')->value($intMaxIndex), ));
		};
		
		# Re-connect if session has expired and retry.
		
		if ($@) {
	
			$ErrorMessage = $@;
		}	
		elsif ($result->valueof("//${strMethod}Result/SuccessFlag")) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
		}	
			
		if (($ErrorMessage =~ m/Expired/) || 
			($ErrorMessage =~ m/timeout/)) { 
		
			QCCommonDisconnect();
			$strClientId = QCCommonConnect();
			redo if $retry--;
		}
	}
	continue { $ErrorMessage = $@ }
	
   	LogStatus($strMethod, $result, $start, $strQuery) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/MultipleReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# QCProxy - Test Plan Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# TestPlanFolderAdd - Add a new folder to the test plan.
#
# Usage:	TestPlanFolderAdd($strSubjectPath, $strFolderName)
# Returns:	0 if successful
#
# Example:	TestPlanFolderAdd('Service Manager\\AutoTests', '~My New Folder')
#---------------------------------------------------------------------------------------------------#
sub TestPlanFolderAdd
{
	my $strSubjectPath 	= shift;	# Parent of folder to be added
	my $strFolderName 	= shift;	# Folder name

	my $strMethod = 'TestPlanFolderAdd';

	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
			SOAP::Data->name('strFolderName')->value($strFolderName), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
				SOAP::Data->name('strFolderName')->value($strFolderName), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }
	
   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestAdd - Add a new test case to a folder in the test plan.
#
# Usage:	TestPlanTestAdd($strSubjectPath, $strTestName, $XML)
# Returns:	Test case ID
#
# Notes:	1. 	Will always update an exiting test (blForce = 1) and assumes target folder already
#				exists (blCreateFolder = 0 and $strFolderName = '').
#			2.	$XML <TS_NAME> may not contain embedded spaces nor special characters, and
#				must match $strTestName exactly.
#			3.	$XML requires a valid <TS_STATUS> value to be specified.
#
# Example:	TestPlanTestAdd('Service Manager\\AutoTests\\~My New Folder', 'MyNewTest', 
#				'<?xml version="1.0" encoding="UTF-8"?><Root><DATA>' .
#				'<TS_NAME>MyNewTest</TS_NAME>' .
#				'<TS_DESCRIPTION>My new test description...</TS_DESCRIPTION>' .
#				'<TS_STATUS>Ready</TS_STATUS>' .
#				'<TS_USER_05>Automated</TS_USER_05>' .
#				'</DATA></Root>')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestAdd
{
	my $strSubjectPath 	= shift;	# Parent of test to be added
	my $strTestName 	= shift;	# Test name
	my $XML 			= shift;	# XML defining the test contents
	
	my $strXML = MKDoc::XML::Encode->process($XML);	# Properly encoded for consumption

	my $blForce 		= 1;						# Always update an existing test
	
	my $blCreateFolder 	= 0;						# Don't create folders
	my $strFolderName 	= '';
	
	my $strMethod = 'TestPlanTestAdd';
	
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
			SOAP::Data->name('strTestName')->value($strTestName),
 			SOAP::Data->name('strFolderName')->value($strFolderName),
			SOAP::Data->name('blForce')->value($blForce),
			SOAP::Data->name('blCreateFolder')->value($blCreateFolder), 
 			SOAP::Data->name('strXML')->value($strXML),	));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
				SOAP::Data->name('strTestName')->value($strTestName),
				SOAP::Data->name('strFolderName')->value($strFolderName),
				SOAP::Data->name('blForce')->value($blForce),
				SOAP::Data->name('blCreateFolder')->value($blCreateFolder), 
				SOAP::Data->name('strXML')->value($strXML),	));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestStepAdd - Add a new design step to a test case in the test plan.
#
# Usage:	TestPlanTestStepAdd($strSubjectPath, $strTestName, $XML)
# Returns:	0 if successful
#
# Notes:	1. 	Will always update an exiting test (blForce = 1) and assumes target folder already
#				exists (blCreateFolder = 0 and $strFolderName = '').
#			2.	$XML <TS_NAME> may not contain embedded spaces nor special characters, and
#				must match $strTestName exactly.
#			3.	$XML requires a valid <TS_STATUS> value to be specified.
#
# Example:	TestPlanTestStepAdd('Service Manager\\AutoTests\\~My New Folder', 'MyNewTest', 
#				'<?xml version="1.0" encoding="UTF-8"?><ROOT><DATA>' .
#				'<DS_STEP_NAME>Test Step 01</DS_STEP_NAME>' .
#				'<DS_EXPECTED>Expected Result</DS_EXPECTED>' .
#				'<DS_DESCRIPTION>Description</DS_DESCRIPTION>' .
#				'</DATA></ROOT>')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestStepAdd
{
	my $strSubjectPath 	= shift;	# Parent of test to be added
	my $strTestName 	= shift;	# Test name
	my $XML 			= shift;	# XML defining the test contents
	
	my $strXML = MKDoc::XML::Encode->process($XML);	# Properly encoded for consumption

	my $strMethod = 'TestPlanTestStepAdd';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';

	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
			SOAP::Data->name('strTestName')->value($strTestName),
			SOAP::Data->name('strXML')->value($strXML), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
				SOAP::Data->name('strTestName')->value($strTestName),
				SOAP::Data->name('strXML')->value($strXML), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }	

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestGet - Get the list of tests contained in a specified folder.
#
# Usage:	TestPlanTestGet($strFolderName)
# Returns:	List of tests contained in the specified folder (Test Name, one per line)
#
# Example:	TestPlanTestGet('Service Manager\\AutoTests\\~My New Folder')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestGet
{
	my $strFolderName = shift;		# Containing folder of which to retrieve tests

	my $strMethod = 'TestPlanTestGet';

	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strFolderName')->value($strFolderName), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strFolderName')->value($strFolderName), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	my $TestCases = $result->valueof("//${strMethod}Result/MultipleReturnValue");
	$TestCases =~ s/~/\n/g;
	return $TestCases;
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestDescriptionGet - Get the descriptions of all design steps for a test case.
#
# Usage:	TestPlanTestDescriptionGet($strSubjectPath, $strTestName)
# Returns:	Description of each design step in the test, one per line
#
# Example:	TestPlanTestDescriptionGet('Service Manager\\AutoTests\\~My New Folder', 'MyNewTest02')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestDescriptionGet
{
	my $strFolderName 	= shift;		# Folder containing the test
	my $strTestName 	= shift;		# Test name
	
	my $strMethod = 'TestPlanTestDescriptionGet';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strTestName')->value($strTestName),
			SOAP::Data->name('strFolderName')->value($strFolderName), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strTestName')->value($strTestName),
				SOAP::Data->name('strFolderName')->value($strFolderName), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	my $TestDescription = $result->valueof("//${strMethod}Result/MultipleReturnValue");
	$TestDescription =~ s/~/\n/g;
	return $TestDescription;
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestStepGet - Get the name, expected result, and description of all steps in a test.
#
# Usage:	TestPlanTestStepGet($strSubjectPath, $strTestName)
# Returns:	List of all design steps in the test, one per line in the following format:
#			{step name}|{expected result}|{description}
#
# Example:	TestPlanTestStepGet('Service Manager\\AutoTests\\~My New Folder', 'MyNewTest02')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestStepGet
{
	my $strFolderName 	= shift;	# Folder containing the test
	my $strTestName 	= shift;	# Test name
	
	my $strMethod = 'TestPlanTestStepGet';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strFolderName')->value($strFolderName),
			SOAP::Data->name('strTestName')->value($strTestName), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strFolderName')->value($strFolderName),
				SOAP::Data->name('strTestName')->value($strTestName), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	my $TestSteps = $result->valueof("//${strMethod}Result/MultipleReturnValue");
	$TestSteps =~ s/~/\n/g;
	return $TestSteps;
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestStepRemove - Remove a specified design step within a test case..
#
# Usage:	TestPlanTestStepRemove($strSubjectPath, $strTestName, $strStepName)
# Returns:	0 if successful
#
# Example:	TestPlanTestStepRemove('Service Manager\\AutoTests\\~My New Folder', 'MyNewTest02', 
#				'Test Step 02')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestStepRemove
{
	my $strSubjectPath 	= shift;	# Parent of test to be added
	my $strTestName 	= shift;	# Test name
	my $strStepName 	= shift;	# Test step name
	
	my $strMethod = 'TestPlanTestStepRemove';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
			SOAP::Data->name('strTestName')->value($strTestName),
			SOAP::Data->name('strStepName')->value($strStepName), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strSubjectPath')->value($strSubjectPath),
				SOAP::Data->name('strTestName')->value($strTestName),
				SOAP::Data->name('strStepName')->value($strStepName), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# TestPlanTestStepRemoveAll - Remove all design steps for a test case.
#
# Usage:	TestPlanTestStepRemoveAll($strSubjectPath, $strTestName)
# Returns:	0 if successful
#
# Example:	TestPlanTestStepRemoveAll('Service Manager\\AutoTests\\~My New Folder', 'MyNewTest02')
#---------------------------------------------------------------------------------------------------#
sub TestPlanTestStepRemoveAll
{
	my $strFolderName 	= shift;	# Folder containing the test
	my $strTestName 	= shift;	# Test name
	my $strStepName 	= shift;	# Test step name
	
	my $strMethod = 'TestPlanTestStepRemoveAll';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strFolderName')->value($strFolderName),
			SOAP::Data->name('strTestName')->value($strTestName), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strFolderName')->value($strFolderName),
				SOAP::Data->name('strTestName')->value($strTestName), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# QCProxy - Test Lab Subroutines
#---------------------------------------------------------------------------------------------------#

#---------------------------------------------------------------------------------------------------#
# TestLabTestInstanceCreate - Add a test instance to a test set in the test lab.
#
# Usage:	TestLabTestInstanceCreate($strFolderPath, $strTestSetName, $strDesignTestId, $XML)
# Returns:	Test instance ID
#
# Notes:	1. 	Test Lab parent is "Service Manager" (with a space), whereas Test Plan parent is
#				"Service Manager" (no space).
#
# Example:	TestLabTestInstanceCreate('Service Manager\\AutoTests\\~My New Folder', 'MyNewTestSet', $TestId, 
#				'<?xml version="1.0" encoding="UTF-8"?><DATA>' . 
#				'<RN_USER_13>Build 123</RN_USER_13>' .
#				'<RN_USER_16>Tomcat 5.5;IE 8.x</RN_USER_16>' .
#				'</DATA>')
#---------------------------------------------------------------------------------------------------#
sub TestLabTestInstanceCreate
{
	my $strFolderPath 		= shift;	# Parent of test to be added
	my $strTestSetName	 	= shift;	# Test set name
	my $strDesignTestId 	= shift;	# Test case ID
	my $XML 				= shift;	# XML defining the test instance contents
	
	my $strXML = MKDoc::XML::Encode->process($XML);	# Properly encoded for consumption
	
	my $strMethod = 'TestLabTestInstanceCreate';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';

	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strFolderPath')->value($strFolderPath),
			SOAP::Data->name('strTestSetName')->value($strTestSetName),
			SOAP::Data->name('strDesignTestId')->value($strDesignTestId),
			SOAP::Data->name('strFields')->value($strXML), ));
	};
		
	my $ErrorMessage = $@;

	open(MYFILE, '>r2qc.log');
	print MYFILE Dumper($result);
	close(MYFILE);
	
	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strFolderPath')->value($strFolderPath),
				SOAP::Data->name('strTestSetName')->value($strTestSetName),
				SOAP::Data->name('strDesignTestId')->value($strDesignTestId),
				SOAP::Data->name('strFields')->value($strXML), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# TestLabRunCreate - Add a test run for a single test case to a test set in the test lab.
#
# Usage:	TestLabRunCreate($strFolderPath, $strTestSetName, $strTestId, $strTestInstanceId, 
#				$strRunStatus, $XML)
# Returns:	Test run ID
#
# Notes:	1.	Test Lab parent is "Service Manager" (with a space), whereas Test Plan parent is
#				"ServiceManager" (no space).
#
# Example:	TestLabRunCreate('Service Manager\\AutoTests\\~My New Folder', 'MyNewTestSet', 
#				$TestId, 'Passed', 
#				'<?xml version="1.0" encoding="UTF-8"?><DATA>' .
#				'<RN_USER_13>Build 123</RN_USER_13>' .
#				'<RN_USER_16>Tomcat 5.5;IE 8.x</RN_USER_16>' .
#				'<RN_DURATION>180</RN_DURATION>' .
#				'</DATA>')
#---------------------------------------------------------------------------------------------------#
sub TestLabRunCreate
{
	my $strFolderPath 		= shift;	# Parent of test to be added
	my $strTestSetName	 	= shift;	# Test set name
	my $strTestId			= shift;	# Test case ID
	my $strTestInstanceId	= shift;	# Test instance ID
	my $strRunStatus		= shift;	# Test run status 
	my $XML 				= shift;	# XML defining the run contents
	
	my $strXML = MKDoc::XML::Encode->process($XML);	# Properly encoded for consumption

	my $blCreateIfNeeded	= 0;		# Create a new test instance, if needed
	
	my $strMethod = 'TestLabRunCreate';
	
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
    # If strRunStatus eq "No Run", then do nothing and return a runID of "".
    
    if ($strRunStatus eq 'No Run') {
        
        return $result;
    }	
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strFolderPath')->value($strFolderPath),
			SOAP::Data->name('strTestSetName')->value($strTestSetName),
			SOAP::Data->name('strTestId')->value($strTestId),
			SOAP::Data->name('strTestInstanceId')->value($strTestInstanceId),
			SOAP::Data->name('strRunStatus')->value($strRunStatus),
			SOAP::Data->name('blCreateIfNeeded')->value($blCreateIfNeeded),
			SOAP::Data->name('strFields')->value($strXML), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strFolderPath')->value($strFolderPath),
				SOAP::Data->name('strTestSetName')->value($strTestSetName),
				SOAP::Data->name('strTestId')->value($strTestId),
				SOAP::Data->name('strTestInstanceId')->value($strTestInstanceId),
				SOAP::Data->name('strRunStatus')->value($strRunStatus),
				SOAP::Data->name('blCreateIfNeeded')->value($blCreateIfNeeded),
				SOAP::Data->name('strFields')->value($strXML), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# TestLabRunStatusSet - Set the status of a test run.
#
# Usage:	TestLabRunStatusSet($strRunId, $strRunStatus, $XML)
# Returns:	0 if successful
#
# Example:	TestLabRunStatusSet($RunId, 'Failed', 
#				'<?xml version="1.0" encoding="UTF-8"?><DATA>' .
#				'<RN_USER_13>Build 123</RN_USER_13>' .
#				'<RN_USER_16>Tomcat 5.5;IE 8.x</RN_USER_16>' .
#				'<RN_DURATION>180</RN_DURATION>' .
#				'</DATA>')
#---------------------------------------------------------------------------------------------------#
sub TestLabRunStatusSet
{
	my $strRunId 			= shift;				# Test run ID
	my $strRunStatus		= shift;				# Test run status 
	my $XML 				= shift;				# XML defining the run contents
	
	my $strXML = MKDoc::XML::Encode->process($XML);	# Properly encoded for consumption
	
	my $strMethod = 'TestLabRunStatusSet2';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
    # If strRunStatus eq "No Run", then do nothing and return 0.
    
    if ($strRunStatus eq 'No Run') {
        
        return 0;
    }
    
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strRunId')->value($strRunId),
			SOAP::Data->name('strRunStatus')->value($strRunStatus),
			SOAP::Data->name('strFields')->value($strXML), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strRunId')->value($strRunId),
				SOAP::Data->name('strRunStatus')->value($strRunStatus),
				SOAP::Data->name('strFields')->value($strXML), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# TestLabRunStepCreate - Set the status of a single test design step in a specified test run.
#
# Usage:	TestLabRunStepCreate($strRunId, $strStepName, $strStepDesc)
# Returns:	0 if successful
#
# Example:	TestLabRunStepCreate($RunId, 'Test Step 03', 'New test step description')
#---------------------------------------------------------------------------------------------------#
sub TestLabRunStepCreate
{
	my $strRunId 		= shift;				# Test run ID
	my $strStepName 	= shift;				# Test design step name
	my $strStepDesc		= shift;				# Test design step description 
	
	my $strMethod = 'TestLabRunStepCreate';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
		$result = $soap->call(
			SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
			SOAP::Data->name('strClientId')->value($strClientId),
			SOAP::Data->name('strRunId')->value($strRunId),
			SOAP::Data->name('strCommentName')->value($strStepName),
			SOAP::Data->name('strComment')->value($strStepDesc), ));
	};
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
			$result = $soap->call(
				SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
				SOAP::Data->name('strClientId')->value($strClientId),
				SOAP::Data->name('strRunId')->value($strRunId),
				SOAP::Data->name('strCommentName')->value($strStepName),
				SOAP::Data->name('strComment')->value($strStepDesc), ));
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }
   
   	LogStatus($strMethod, $result, $start) if $LogLevel;
	
	return $result->valueof("//${strMethod}Result/ActualReturnValue");
}

#---------------------------------------------------------------------------------------------------#
# TestLabRunStepStatusSet - Set the status of a single test design step in a specified test run.
#
# Usage:	TestLabRunStepStatusSet($strRunId, $strStepName, $strStepStatus)
# Returns:	0 if successful
#
# Example:	TestLabRunStepStatusSet($RunId, 'Test Step 02', 'Failed')
#---------------------------------------------------------------------------------------------------#
sub TestLabRunStepStatusSet
{
	my $strRunId 			= shift;				# Test run ID
	my $strStepName		 	= shift;				# Test design step name
	my $strStepStatus		= shift;				# Test design step status 
	
	# If strStepStatus eq "No Run", then do nothing and return 0.
	
	if ($strStepStatus eq 'No Run') {
		
		return 0;
	}
	
	my $strMethod = 'TestLabRunStepStatusSet';
		
	my $retry = 3;					# Number of times to retry.
	my $start = gettimeofday();
	
	my $result  = '';
	my $success = '';
	
	eval { no warnings;
		
        $result = $soap->call(
            SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
            SOAP::Data->name('strClientId')->value($strClientId),
            SOAP::Data->name('strRunId')->value($strRunId),
            SOAP::Data->name('strStepName')->value($strStepName),
            SOAP::Data->name('strStepStatus')->value($strStepStatus), ));			
	};
		
	## TODO - If failed with 'Steps Not Found', call TestLabRunStepCreate & retry.  This begs
	## the question of what to name the steps and what happens when the names change.
		
	my $ErrorMessage = $@;

	while ($@ && ($retry--)) {
	
		eval { no warnings;

			print "Results2QC (" . RunTime() ."s):    $strMethod retrying after error $ErrorMessage" if $LogLevel;
			
            $result = $soap->call(
                SOAP::Data->name($strMethod)->attr( { xmlns => $uri . "/" } ) => (
                SOAP::Data->name('strClientId')->value($strClientId),
                SOAP::Data->name('strRunId')->value($strRunId),
                SOAP::Data->name('strStepName')->value($strStepName),
                SOAP::Data->name('strStepStatus')->value($strStepStatus), ));           
		};
		
		# Re-connect if session has expired and retry.
		
		unless ($result->valueof("//${strMethod}Result/SuccessFlag") == 0) {
		
			$ErrorMessage = $result->valueof("//${strMethod}Result/ErrorDescription") . "\n";
			
			if (($ErrorMessage =~ m/Expired/) || 
				($ErrorMessage =~ m/timeout/)) { 
			
				QCCommonDisconnect();
				$strClientId = QCCommonConnect();
				redo if $retry--;
			}
		}
	}
	continue { $ErrorMessage = $@ }

   	LogStatus($strMethod, $result, $start) if $LogLevel;

	return $result->valueof("//${strMethod}Result/SuccessFlag");
}

#---------------------------------------------------------------------------------------------------#
# END of Results2QC.pl
#---------------------------------------------------------------------------------------------------#