<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<testSuite><xsl:for-each select="testSuite/test">
	<test>
		<xsl:attribute name="testName"><xsl:value-of select="@testName"/></xsl:attribute>
		<xsl:attribute name="testDescription"><xsl:value-of select="@testDescription"/></xsl:attribute>
		<xsl:attribute name="testDuration"><xsl:value-of select="@testDuration"/></xsl:attribute>
		<xsl:attribute name="testAutomation">Automated</xsl:attribute>
		<xsl:attribute name="testProduct">{{product}}</xsl:attribute>
		<xsl:attribute name="testModule">{{module}}</xsl:attribute>
		<xsl:attribute name="testKeywords">{{keywords}}</xsl:attribute>
		<xsl:attribute name="testScriptPath">{{qcPlanSubject}}.<xsl:value-of select="@testSetName"/>.<xsl:value-of select="@testName"/></xsl:attribute>
		<xsl:attribute name="testSetPath">{{qcLabRoot}}.<xsl:value-of select="@testSetName"/>.<xsl:value-of select="@testName"/></xsl:attribute>
		<xsl:attribute name="testSetNotPassedPath">{{qcLabRootNotPassed}}.<xsl:value-of select="@testSetName"/>.<xsl:value-of select="@testName"/></xsl:attribute>
		<xsl:attribute name="testSetName">.<xsl:value-of select="@testSetName"/>.<xsl:value-of select="@testName"/></xsl:attribute>
		<xsl:choose>
			<xsl:when test="@testExecutionStatus='Failed'">
				<xsl:attribute name="testExecutionStatus">Failed</xsl:attribute>
				<xsl:attribute name="testStatus">Review</xsl:attribute>
				</xsl:when>
			<xsl:when test="@testExecutionStatus='Not Completed'">
				<xsl:attribute name="testExecutionStatus">Not Completed</xsl:attribute>
				<xsl:attribute name="testStatus">Repair</xsl:attribute>
				</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="testExecutionStatus">Passed</xsl:attribute>
				<xsl:attribute name="testStatus">Ready</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		<testRun>
			<xsl:attribute name="runName">{{release}} {{suite}} for {{build}} on {{config}}</xsl:attribute>
			<xsl:attribute name="runBuild">{{build}}</xsl:attribute>
			<xsl:attribute name="runConfig">{{config}}</xsl:attribute>
			<xsl:attribute name="runEnvironment">{{environment}}</xsl:attribute>
			<xsl:attribute name="runDuration"><xsl:value-of select="testRun/@runDuration"/></xsl:attribute>
			<xsl:attribute name="runStatus"><xsl:value-of select="testRun/@runStatus"/></xsl:attribute>
			</testRun>
		<testSteps><xsl:for-each select="testSteps/step">
			<step>
				<xsl:attribute name="stepName"><xsl:value-of select="@stepName"/></xsl:attribute>
				<xsl:attribute name="stepStatus"><xsl:value-of select="@stepStatus"/></xsl:attribute>
				<xsl:attribute name="stepDuration"><xsl:value-of select="@stepDuration"/></xsl:attribute>
				<xsl:value-of select="."/>
				</step>
				</xsl:for-each>
			</testSteps>
		</test>
	</xsl:for-each>
</testSuite>
</xsl:template>
</xsl:stylesheet>