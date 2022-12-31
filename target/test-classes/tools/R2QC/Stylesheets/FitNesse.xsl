<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<testSuite><xsl:for-each select="testResults/result">
	<test>
		<xsl:attribute name="testName"><xsl:value-of select="relativePageName"/></xsl:attribute>
		<xsl:attribute name="testDescription"/>
		<xsl:variable  name="testDuration">0</xsl:variable>
		<xsl:attribute name="testDuration"><xsl:value-of select="$testDuration"/></xsl:attribute>
		<xsl:attribute name="testAutomation">Automated</xsl:attribute>
		<xsl:attribute name="testProduct">{{product}}</xsl:attribute>
		<xsl:attribute name="testModule">{{module}}</xsl:attribute>
		<xsl:attribute name="testKeywords">TestTool=FitNesse {{keywords}}</xsl:attribute>
		<xsl:attribute name="testScriptPath">{{qcPlanSubject}}.<xsl:value-of select="relativePageName"/></xsl:attribute>
		<xsl:attribute name="testSetPath">{{qcLabRoot}}.<xsl:value-of select="relativePageName"/></xsl:attribute>
		<xsl:attribute name="testSetNotPassedPath">{{qcLabRootNotPassed}}.<xsl:value-of select="relativePageName"/></xsl:attribute>
		<xsl:attribute name="testSetName">.<xsl:value-of select="relativePageName"/></xsl:attribute>
		<xsl:choose>
			<xsl:when test="counts/exceptions > 0">
				<xsl:attribute name="testExecutionStatus">Not Completed</xsl:attribute>
				<xsl:attribute name="testStatus">Repair</xsl:attribute>
				</xsl:when>
			<xsl:when test="counts/wrong > 0">
				<xsl:attribute name="testExecutionStatus">Failed</xsl:attribute>
				<xsl:attribute name="testStatus">Review</xsl:attribute>
				</xsl:when>
			<xsl:when test="counts/right > 0">
				<xsl:attribute name="testsExecutionStatus">Passed</xsl:attribute>
				<xsl:attribute name="testStatus">Ready</xsl:attribute>
				</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="testExecutionStatus">No Run</xsl:attribute>
				<xsl:attribute name="testStatus">Review</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		<testRun>
			<xsl:attribute name="runName">{{release}} {{suite}} for {{build}} on {{config}}</xsl:attribute>
			<xsl:attribute name="runBuild">{{build}}</xsl:attribute>
			<xsl:attribute name="runConfig">{{config}}</xsl:attribute>
			<xsl:attribute name="runEnvironment">{{environment}}</xsl:attribute>
			<xsl:attribute name="runDuration"><xsl:value-of select="$testDuration"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="counts/exceptions > 0">
					<xsl:attribute name="runStatus">Not Completed</xsl:attribute>
					</xsl:when>
				<xsl:when test="counts/wrong > 0">
					<xsl:attribute name="runStatus">Failed</xsl:attribute>
					</xsl:when>
				<xsl:when test="counts/right > 0">
					<xsl:attribute name="runStatus">Passed</xsl:attribute>
					</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="runStatus">No Run</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</testRun>
		<testSteps>
			<step>
				<xsl:attribute name="stepName"><xsl:value-of select="relativePageName"/></xsl:attribute>
				<xsl:choose>
					<xsl:when test="counts/exceptions > 0">
						<xsl:attribute name="stepStatus">Not Completed</xsl:attribute>
						</xsl:when>
					<xsl:when test="counts/wrong > 0">
						<xsl:attribute name="stepStatus">Failed</xsl:attribute>
						</xsl:when>
					<xsl:when test="counts/right > 0">
						<xsl:attribute name="stepStatus">Passed</xsl:attribute>
						</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="stepStatus">No Run</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				<xsl:value-of select="content"/>
				</step>
			</testSteps>
		</test>
	</xsl:for-each>
</testSuite>
</xsl:template>
</xsl:stylesheet>