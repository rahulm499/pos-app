<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:msxsl="urn:schemas-microsoft-com:xslt" exclude-result-prefixes="msxsl">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21.0cm" margin-top="1cm" margin-left="2cm" margin-right="2cm" margin-bottom="1cm">
                    <!-- Page template goes here -->
                    <fo:region-body />
                    <fo:region-before region-name="xsl-region-before" extent="3cm"/>
                    <fo:region-after region-name="xsl-region-after" extent="4cm"/>

                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="A4">
                <!-- Page content goes here -->
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block>
                        <fo:table table-layout="auto">
                            <fo:table-column column-width="9cm"/>
                            <fo:table-column column-width="8cm"/>
                            <fo:table-body>
                                <fo:table-row font-size="18pt" line-height="30px" background-color="#1f6a9c" color="white" font-weight = "bold">
                                    <fo:table-cell padding-left="5pt">
                                        <fo:block>
                                            Increff
                                        </fo:block>
                                    </fo:table-cell>

                                    <fo:table-cell>
                                        <fo:block text-align="right">
                                            INVOICE
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell padding-left="5pt" padding-top="5pt">
                                        <fo:block>
                                            Enzyme Tech Park, HSR Layout&#x2028;
                                            Bengaluru, KA&#x2028;
                                            560034
                                        </fo:block>
                                    </fo:table-cell>

                                </fo:table-row>

                            </fo:table-body>
                        </fo:table>
                    </fo:block>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block line-height="20pt">
                        <fo:block font-weight="bold" text-align="center">
                            HAVE A NICE DAY
                        </fo:block>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" line-height="20pt">
                    <xsl:apply-templates />
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="invoice">
        <fo:block>

        </fo:block>
        <fo:block space-before="120pt" width="17cm" >
            <fo:table >
                <fo:table-column column-width="6.5cm"/>

                <fo:table-column column-width="7cm"/>
                <fo:table-column column-width="3cm"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
                            <fo:block>
                                <fo:inline font-weight="bold">Bill To</fo:inline>
                                <xsl:call-template name="address">
                                    <xsl:with-param name="address" select="./address[@type='billto']"></xsl:with-param>
                                </xsl:call-template>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell>
                            <fo:block text-align="left" margin-left="100px" padding-left="20px">
                                <fo:inline font-weight="bold">Invoice #</fo:inline>&#x2028;
                                <fo:inline font-weight="bold">Invoice Date</fo:inline>&#x2028;
                                <fo:inline font-weight="bold">Invoice Time</fo:inline>&#x2028;
                            </fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
                            <fo:block text-align="right">
                                <xsl:value-of select="./invoicenumber"></xsl:value-of>&#x2028;
                                <xsl:value-of select="./invoicedate"></xsl:value-of>&#x2028;
                                <xsl:value-of select="./invoicetime"></xsl:value-of>&#x2028;

                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:block>
        <fo:block space-before="50pt">
            <fo:table line-height="30px" table-layout="auto">
                <fo:table-column column-width="2cm"/>
                <fo:table-column column-width="3cm"/>
                <fo:table-column column-width="5cm"/>
                <fo:table-column column-width="2cm"/>
                <fo:table-column column-width="2cm"/>
                <fo:table-column column-width="3cm"/>
                <fo:table-header>
                    <fo:table-row background-color="#f5f5f5" text-align="center" font-weight="bold">
                        <fo:table-cell border="1px solid #b8b6b6">
                            <fo:block>S.No.</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1px solid #b8b6b6">
                            <fo:block>Barcode</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1px solid #b8b6b6">
                            <fo:block>PRODUCT</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1px solid #b8b6b6">
                            <fo:block>QTY</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1px solid #b8b6b6">
                            <fo:block>UNIT PRICE</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border="1px solid #b8b6b6">
                            <fo:block>TOTAL AMOUNT</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <xsl:apply-templates select="lineitems/lineitem"></xsl:apply-templates>
                    <fo:table-row font-weight="bold">
                        <fo:table-cell number-columns-spanned="5" text-align="right" padding-right="3pt">
                            <fo:block>Total</fo:block>
                        </fo:table-cell>
                        <fo:table-cell  text-align="right" padding-right="3pt" background-color="#f5f5f5" border="1px solid #b8b6b6" >
                            <fo:block>
                                <xsl:value-of select="total" />
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:block>
    </xsl:template>
    <xsl:template name="address">
        <xsl:param name="address"></xsl:param>
        <fo:block>
            <xsl:value-of select="$address/name" />&#x2028;
            <xsl:value-of select="$address/building" />&#x2028;
            <xsl:value-of select="$address/area" />&#160;
            <xsl:value-of select="$address/city" />
        </fo:block>
    </xsl:template>
    <xsl:template match="lineitem">
        <fo:table-row>
            <fo:table-cell border="1px solid #b8b6b6" text-align="center">
                <fo:block>
                    <xsl:value-of select="sno"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1px solid #b8b6b6" text-align="center">
                <fo:block>
                    <xsl:value-of select="barcode"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1px solid #b8b6b6" padding-left="3pt">
                <fo:block>
                    <xsl:value-of select="product"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
                <fo:block>
                    <xsl:value-of select="quantity"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
                <fo:block>
                    <xsl:value-of select="unit-price"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1px solid #b8b6b6" text-align="right" padding-right="3pt">
                <fo:block>
                    <xsl:value-of select="amount"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>

    </xsl:template>
</xsl:stylesheet>