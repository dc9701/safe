@ echo off
cls

rem R2QC.bat - Run our simple BAT test and publish test results to Quality Center.

rem Location of test results.

set RESULT_PATH=C:\Users\phillder\workspace\ipg-iws-autotest\trunk\target\surefire-reports

rem Location of Results2QC.pl

set R2QC_PATH=.
set R2QC=%R2QC_PATH%\Results2QC.pl
set R2QC_RES=%R2QC_PATH%\Stylesheets

rem Set general parameters for Result2QC.pl

set PRODUCT=Gemini
set MODULE=Web Dev: Gemini
set KEYWORDS=Selenium
set STYLESHEET=%R2QC_RES%\SeleniumJunit.xsl
set QC_PROXY=http://g6w2381g.atlanta.hp.com/qcproxy/wsqccommon.asmx
set UPDATE_STEPS=-updateSteps true

rem Delete any previous run.log.

del /Q run.log

rem Determine the current build from TortoiseSVN (use if running on localhost rather than DIS).

set SVN_LOGFILE=%APPDATA%\TortoiseSVN\logfile.txt

find "At revision" "%SVN_LOGFILE%" > currentRevision.tmp
for /F "tokens=5" %%X in (currentRevision.tmp) do set BUILD=r%%X
del /Q currentRevision.tmp

rem Set test run-specific parameters.

set RELEASE=HPC 2012.14
set ITERATION=Stage1_FullRegression_3.6.2014
rem set ITERATION=Stage1_OneDayRegression_3.6.2014

rem Set test suite-specific parameters for Unit tests.

set SUITE=UnitTests
set CONFIG=Dev02 (DIS)
set BUILD=r4572
set TESTPLAN_ROOT=Subject\Firmware\Ink Sub 2.0\%PRODUCT%\AutoTests
set TESTLAB_ROOT=Root\Firmware\2013 Products\%PRODUCT%\Automation\%ITERATION%\AutoTests
set TEST_ROOT=test.cases.instantInk
set TEST_GROUP=HPC Client Scrum
set RESULT_FILES=%RESULT_PATH%\*.xml

echo .
echo == Publish the test results to Quality Center ==
echo .
echo on
perl "%R2QC%" -suite "%SUITE%" -config "%CONFIG%" -product "%PRODUCT%" -release "%RELEASE%" -build "%BUILD%" -qcPlanSubject "%TESTPLAN_ROOT%" -qcLabRoot "%TESTLAB_ROOT%" -testRoot "%TEST_ROOT%" -testGroup "%TEST_GROUP%" -keywords "%KEYWORDS%" -module "%MODULE%" -stylesheet "%STYLESHEET%" -qcProxy "%QC_PROXY%" %UPDATE_STEPS% -resultFiles "%RESULT_FILES%"
@echo off

echo .
echo === Publishing test results complete! ===
