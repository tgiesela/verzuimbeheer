-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: ubuntuglassfish.thuis.local    Database: verzuim
-- ------------------------------------------------------
-- Server version	5.5.52-0ubuntu0.14.04.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ACTIVITEIT`
--

DROP TABLE IF EXISTS `ACTIVITEIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ACTIVITEIT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(45) NOT NULL,
  `duur` varchar(45) NOT NULL COMMENT 'Tijd te besteden aan activiteit',
  `allewerkgevers` int(11) NOT NULL COMMENT 'allewerkgevers geeft aan of de activiteit voor alle werkgevers in die het pakket afnemen wordt uitgevoerd. Indien false, dan wordt in de tabel werkgever_has_activiteit gekeken voor wie het uitgevoerd moet worden',
  `ketenverzuim` int(11) NOT NULL COMMENT 'Activiteit alleen bij ketenverzuim uitvoeren',
  `deadlineperiode` int(11) NOT NULL,
  `deadlineperiodesoort` int(11) NOT NULL COMMENT 'Periodelengte: Dag, Week, Maand',
  `deadlinestartpunt` int(11) NOT NULL COMMENT 'Geeft aan vanaf welk moment de deadline in gaat. Dit kan zijn na aanvang verzuim, na gedeeltelijk herstel of na volledig herstel.',
  `deadlinewaarschuwmoment` int(11) DEFAULT NULL,
  `deadlinewaarschuwmomentsoort` int(11) DEFAULT NULL COMMENT 'Periodelengte: Dag, Week, Maand',
  `repeteerperiode` int(11) DEFAULT NULL,
  `repeteerperiodesoort` int(11) DEFAULT NULL COMMENT 'Periodelengte: Dag, Week, Maand',
  `repeteeraantal` int(11) DEFAULT NULL,
  `normaalverzuim` int(11) NOT NULL,
  `vangnet` int(11) NOT NULL,
  `vangnettype` int(11) DEFAULT NULL,
  `plannaactiviteit` int(11) DEFAULT NULL COMMENT 'Plannen als andere activiteit is afgerond',
  `omschrijving` varchar(250) DEFAULT NULL,
  `verwijdernaherstel` int(11) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ADRES`
--

DROP TABLE IF EXISTS `ADRES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ADRES` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `straat` varchar(50) DEFAULT NULL,
  `huisnummer` varchar(10) DEFAULT NULL,
  `huisnummertoevoeging` varchar(10) DEFAULT NULL,
  `postcode` varchar(12) DEFAULT NULL,
  `plaats` varchar(50) DEFAULT NULL,
  `land` varchar(45) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=33858 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AFDELING`
--

DROP TABLE IF EXISTS `AFDELING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AFDELING` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `werkgever_id` int(11) NOT NULL,
  `naam` varchar(45) NOT NULL,
  `afdelingsid` varchar(45) DEFAULT NULL,
  `contactpersoon_ID` int(11) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `werkgever` (`werkgever_id`),
  KEY `fk_afdeling_contactpersoon1` (`contactpersoon_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1515 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AFDELING_HAS_WERKNEMER`
--

DROP TABLE IF EXISTS `AFDELING_HAS_WERKNEMER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AFDELING_HAS_WERKNEMER` (
  `afdeling_ID` int(11) NOT NULL,
  `werknemer_ID` int(11) NOT NULL,
  `startdatum` date NOT NULL DEFAULT '0000-00-00',
  `einddatum` date DEFAULT NULL,
  `uren` decimal(4,2) DEFAULT NULL,
  PRIMARY KEY (`afdeling_ID`,`werknemer_ID`,`startdatum`),
  KEY `fk_afdeling_has_werknemer_werknemer1` (`werknemer_ID`),
  KEY `fk_afdeling_has_werknemer_afdeling1` (`afdeling_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `APPLICATIEFUNCTIE`
--

DROP TABLE IF EXISTS `APPLICATIEFUNCTIE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APPLICATIEFUNCTIE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `functie_id` varchar(10) NOT NULL,
  `functieomschrijving` varchar(45) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `FUNCTIE` (`functie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ARBODIENST`
--

DROP TABLE IF EXISTS `ARBODIENST`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ARBODIENST` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(50) NOT NULL,
  `vestigingsadres_ID` int(11) DEFAULT NULL,
  `postadres_ID` int(11) DEFAULT NULL,
  `contactpersoon_ID` int(11) DEFAULT NULL,
  `telefoonnummer` varchar(15) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_arbodienst_Adres1` (`vestigingsadres_ID`),
  KEY `fk_arbodienst_contactpersoon1` (`contactpersoon_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BEDRIJFSARTS`
--

DROP TABLE IF EXISTS `BEDRIJFSARTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BEDRIJFSARTS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `achternaam` varchar(50) DEFAULT NULL,
  `voornaam` varchar(20) DEFAULT NULL,
  `geslacht` int(11) DEFAULT NULL,
  `telefoon` varchar(20) DEFAULT NULL,
  `voorletters` varchar(20) DEFAULT NULL,
  `arbodienst_ID` int(11) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BEDRIJFSGEGEVENS`
--

DROP TABLE IF EXISTS `BEDRIJFSGEGEVENS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BEDRIJFSGEGEVENS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(100) DEFAULT NULL,
  `telefoon` varchar(15) DEFAULT NULL,
  `mobiel` varchar(15) DEFAULT NULL,
  `fax` varchar(15) DEFAULT NULL,
  `vestigingsadres_ID` int(11) NOT NULL,
  `postadres_ID` int(11) DEFAULT NULL,
  `emailadres` varchar(50) DEFAULT NULL,
  `website` varchar(50) DEFAULT NULL,
  `kvknr` varchar(45) DEFAULT NULL,
  `bankrekening` varchar(45) DEFAULT NULL,
  `btwnummer` varchar(45) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BTW`
--

DROP TABLE IF EXISTS `BTW`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BTW` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ingangsdatum` datetime NOT NULL,
  `einddatum` datetime DEFAULT NULL,
  `percentage` decimal(5,2) NOT NULL,
  `btwtariefsoort` int(11) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CASCODE`
--

DROP TABLE IF EXISTS `CASCODE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CASCODE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cascode` varchar(20) NOT NULL,
  `omschrijving` varchar(255) DEFAULT NULL,
  `cascodegroep` int(11) NOT NULL,
  `vangnettype` int(11) NOT NULL DEFAULT '2',
  `actief` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cascodegroepix` (`cascodegroep`),
  KEY `cascodeix` (`cascode`)
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CASCODEGROEP`
--

DROP TABLE IF EXISTS `CASCODEGROEP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CASCODEGROEP` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(50) NOT NULL,
  `omschrijving` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CONTACTPERSOON`
--

DROP TABLE IF EXISTS `CONTACTPERSOON`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CONTACTPERSOON` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `achternaam` varchar(50) NOT NULL,
  `voorvoegsel` varchar(10) DEFAULT NULL,
  `voorletters` varchar(10) DEFAULT NULL,
  `voornaam` varchar(45) DEFAULT NULL,
  `geslacht` int(11) DEFAULT NULL,
  `telefoon` varchar(15) DEFAULT NULL,
  `mobiel` varchar(15) DEFAULT NULL,
  `emailadres` varchar(50) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=594 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DIENSTVERBAND`
--

DROP TABLE IF EXISTS `DIENSTVERBAND`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DIENSTVERBAND` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `werkgever_ID` int(11) NOT NULL,
  `werknemer_ID` int(11) NOT NULL,
  `startdatumcontract` date DEFAULT NULL COMMENT 'dit is een dienstverband. Een werknemer kan meerdere opeenvolgende dienstverbanden bij dezelfde werkgever hebben.',
  `einddatumcontract` date DEFAULT NULL,
  `personeelsnummer` varchar(45) DEFAULT NULL,
  `functie` varchar(45) DEFAULT NULL,
  `werkweek` decimal(5,2) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_werkgever` (`werkgever_ID`),
  KEY `ix_werknemer` (`werknemer_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=36849 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DOCUMENTTEMPLATE`
--

DROP TABLE IF EXISTS `DOCUMENTTEMPLATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DOCUMENTTEMPLATE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(50) NOT NULL,
  `padnaam` varchar(250) NOT NULL,
  `omschrijving` varchar(250) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUUR`
--

DROP TABLE IF EXISTS `FACTUUR`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUUR` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `werkgeverid` int(11) NOT NULL,
  `holdingid` int(11) DEFAULT NULL,
  `factuurnr` int(11) NOT NULL,
  `maand` int(11) NOT NULL,
  `jaar` int(11) NOT NULL,
  `aansluitkosten` decimal(18,2) NOT NULL,
  `abonnementskosten` decimal(18,2) NOT NULL,
  `aantalmedewerkers` int(11) NOT NULL,
  `omschrijvingfactuur` varchar(50) NOT NULL,
  `factuurstatus` int(11) NOT NULL,
  `btwpercentagehoog` decimal(5,2) NOT NULL,
  `btwpercentagelaag` decimal(5,2) NOT NULL,
  `aanmaakdatum` date NOT NULL,
  `peildatumaansluitkosten` date DEFAULT NULL,
  `pdflocation` varchar(512) DEFAULT NULL,
  `maandbedragsecretariaat` decimal(18,2) NOT NULL,
  `aansluitkostenperiode` int(11) NOT NULL,
  `abonnementskostenperiode` int(11) NOT NULL,
  `tariefid` int(11) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix1` (`factuurnr`),
  KEY `ixperiode` (`jaar`,`maand`),
  KEY `ixwerkgever` (`werkgeverid`),
  KEY `ixholding` (`holdingid`)
) ENGINE=InnoDB AUTO_INCREMENT=14091 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURBETALING`
--

DROP TABLE IF EXISTS `FACTUURBETALING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURBETALING` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factuurid` int(11) NOT NULL,
  `datum` date NOT NULL,
  `rekeningnummerbetaler` varchar(255) NOT NULL,
  `bedrag` decimal(18,2) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1054 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURCATEGORIE`
--

DROP TABLE IF EXISTS `FACTUURCATEGORIE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURCATEGORIE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `omschrijving` varchar(250) NOT NULL,
  `btwcategorie` int(11) NOT NULL,
  `factuurkopid` int(11) NOT NULL,
  `isomzet` int(11) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURITEM`
--

DROP TABLE IF EXISTS `FACTUURITEM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURITEM` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `werkgeverid` int(11) NOT NULL,
  `holdingid` int(11) DEFAULT NULL,
  `datum` date NOT NULL,
  `factuurcategorieid` int(11) NOT NULL,
  `bedrag` decimal(18,2) NOT NULL,
  `userid` int(11) NOT NULL,
  `omschrijving` varchar(255) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1024 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURKOP`
--

DROP TABLE IF EXISTS `FACTUURKOP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURKOP` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `omschrijving` varchar(50) NOT NULL,
  `prioriteit` int(11) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURREGELBEZOEK`
--

DROP TABLE IF EXISTS `FACTUURREGELBEZOEK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURREGELBEZOEK` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factuurid` int(11) NOT NULL,
  `werkzaamhedenid` int(11) NOT NULL,
  `uurkosten` decimal(18,2) NOT NULL,
  `uurtarief` decimal(18,2) NOT NULL,
  `kilometerkosten` decimal(18,2) NOT NULL,
  `kilometertarief` decimal(18,2) NOT NULL,
  `overigekosten` decimal(18,2) NOT NULL,
  `casemanagementkosten` decimal(18,2) NOT NULL,
  `vastekosten` decimal(18,2) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix2` (`werkzaamhedenid`),
  UNIQUE KEY `ix1` (`factuurid`,`werkzaamhedenid`)
) ENGINE=InnoDB AUTO_INCREMENT=13683 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURREGELITEM`
--

DROP TABLE IF EXISTS `FACTUURREGELITEM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURREGELITEM` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factuurid` int(11) NOT NULL,
  `factuuritemid` int(11) NOT NULL,
  `bedrag` decimal(18,2) NOT NULL,
  `btwbedrag` decimal(18,2) NOT NULL,
  `btwcategorie` int(11) DEFAULT NULL,
  `btwpercentage` decimal(18,2) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix2` (`factuuritemid`),
  UNIQUE KEY `ix1` (`factuurid`,`factuuritemid`)
) ENGINE=InnoDB AUTO_INCREMENT=2491 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FACTUURREGELSECRETARIAAT`
--

DROP TABLE IF EXISTS `FACTUURREGELSECRETARIAAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FACTUURREGELSECRETARIAAT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `factuurid` int(11) NOT NULL,
  `weeknummer` int(11) NOT NULL,
  `secretariaatskosten` decimal(18,2) NOT NULL,
  `uurtarief` decimal(18,2) NOT NULL,
  `overigekosten` decimal(18,2) NOT NULL,
  `werkzaamhedenid` int(11) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix2` (`werkzaamhedenid`),
  UNIQUE KEY `ix1` (`factuurid`,`werkzaamhedenid`)
) ENGINE=InnoDB AUTO_INCREMENT=74931 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `GEBRUIKER`
--

DROP TABLE IF EXISTS `GEBRUIKER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GEBRUIKER` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gebruikersnaam` varchar(45) NOT NULL,
  `status` int(11) NOT NULL,
  `inlogfouten` int(11) NOT NULL,
  `laatstepoging` datetime DEFAULT NULL,
  `passwordhash` char(64) DEFAULT NULL,
  `emailadres` varchar(250) DEFAULT NULL,
  `voornaam` varchar(45) DEFAULT NULL,
  `achternaam` varchar(100) DEFAULT NULL,
  `tussenvoegsel` varchar(45) DEFAULT NULL,
  `alleklanten` int(11) NOT NULL DEFAULT '0',
  `aduser` int(11) NOT NULL DEFAULT '0',
  `domainname` varchar(45) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ix_gebruikersnaam` (`gebruikersnaam`),
  UNIQUE KEY `ix_emailadres` (`emailadres`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `GEBRUIKER_ROL`
--

DROP TABLE IF EXISTS `GEBRUIKER_ROL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GEBRUIKER_ROL` (
  `gebruikerid` int(11) NOT NULL,
  `rolid` int(11) NOT NULL,
  PRIMARY KEY (`gebruikerid`,`rolid`),
  KEY `rol` (`rolid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `GEBRUIKER_WERKGEVER`
--

DROP TABLE IF EXISTS `GEBRUIKER_WERKGEVER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GEBRUIKER_WERKGEVER` (
  `gebruikerid` int(11) NOT NULL AUTO_INCREMENT,
  `werkgeverid` int(11) NOT NULL DEFAULT '0' COMMENT 'If -1 then all employers are available',
  PRIMARY KEY (`gebruikerid`,`werkgeverid`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1 COMMENT='Table used to link a user to a specific EMPLOYER. ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `HOLDING`
--

DROP TABLE IF EXISTS `HOLDING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HOLDING` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(45) NOT NULL,
  `vestigingsadres_ID` int(11) NOT NULL,
  `postadres_ID` int(11) DEFAULT NULL,
  `contactpersoon_ID` int(11) DEFAULT NULL,
  `telefoon` varchar(20) DEFAULT NULL,
  `factuuradres_ID` int(11) DEFAULT NULL,
  `BTWnummer` varchar(45) DEFAULT NULL,
  `Debiteurnummer` int(11) DEFAULT NULL,
  `Detailsecretariaat` int(11) DEFAULT '0',
  `Emailadresfactuur` varchar(254) DEFAULT NULL,
  `Factureren` int(11) DEFAULT '0',
  `Factuurtype` int(11) DEFAULT NULL,
  `contactpersoonfactuur_ID` int(11) DEFAULT NULL,
  `startdatumcontract` datetime DEFAULT NULL,
  `einddatumcontract` datetime DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `KANWEG`
--

DROP TABLE IF EXISTS `KANWEG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KANWEG` (
  `burgerservicenr` varchar(10) NOT NULL,
  `werkweek` decimal(5,2) NOT NULL,
  `werkgeverid` int(11) NOT NULL,
  PRIMARY KEY (`burgerservicenr`,`werkgeverid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `KFCUREN`
--

DROP TABLE IF EXISTS `KFCUREN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `KFCUREN` (
  `WerkgeverNaam` varchar(255) NOT NULL,
  `WerkgeverAfk` varchar(45) NOT NULL,
  `Werknemer` varchar(255) NOT NULL,
  `personeelsnummer` varchar(10) NOT NULL,
  `nr` varchar(45) DEFAULT NULL,
  `dummy` varchar(45) DEFAULT NULL,
  `Uren` decimal(18,2) NOT NULL,
  `Kostenplaats` varchar(45) DEFAULT NULL,
  `Omschrijving` varchar(45) DEFAULT NULL,
  `booekjaar` varchar(45) DEFAULT NULL,
  `soortperiodetabel` varchar(45) DEFAULT NULL,
  `periode` varchar(45) DEFAULT NULL,
  `periodeomschrijving` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`personeelsnummer`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OE`
--

DROP TABLE IF EXISTS `OE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oeniveau_id` int(11) NOT NULL,
  `parentoe_id` int(11) DEFAULT NULL,
  `werkgever_id` int(11) DEFAULT NULL,
  `naam` varchar(50) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=386 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OENIVEAU`
--

DROP TABLE IF EXISTS `OENIVEAU`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OENIVEAU` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oeniveau` int(11) NOT NULL,
  `naam` varchar(50) NOT NULL,
  `parentoeniveau_id` int(11) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OEREPORT`
--

DROP TABLE IF EXISTS `OEREPORT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OEREPORT` (
  `id` int(11) NOT NULL,
  `oeniveau_id` int(11) DEFAULT NULL,
  `parentoe_id` int(11) DEFAULT NULL,
  `werkgever_id` int(11) DEFAULT NULL,
  `naam` varchar(45) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAKKET`
--

DROP TABLE IF EXISTS `PAKKET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAKKET` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(50) NOT NULL,
  `omschrijving` varchar(250) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PAKKET_HAS_ACTIVITEIT`
--

DROP TABLE IF EXISTS `PAKKET_HAS_ACTIVITEIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PAKKET_HAS_ACTIVITEIT` (
  `pakket_ID` int(11) NOT NULL,
  `activiteit_ID` int(11) NOT NULL,
  PRIMARY KEY (`activiteit_ID`,`pakket_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ROL`
--

DROP TABLE IF EXISTS `ROL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ROL` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rolid` varchar(10) DEFAULT NULL,
  `omschrijving` varchar(45) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rolid_UNIQUE` (`rolid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ROL_APPLICATIEFUNCTIE`
--

DROP TABLE IF EXISTS `ROL_APPLICATIEFUNCTIE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ROL_APPLICATIEFUNCTIE` (
  `rol_id` int(11) NOT NULL,
  `applicatiefunctie_id` int(11) NOT NULL,
  PRIMARY KEY (`rol_id`,`applicatiefunctie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TARIEF`
--

DROP TABLE IF EXISTS `TARIEF`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TARIEF` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `werkgeverId` int(11) DEFAULT NULL,
  `holdingId` int(11) DEFAULT NULL,
  `ingangsdatum` date NOT NULL,
  `einddatum` date DEFAULT NULL,
  `UurtariefNormaal` decimal(18,2) NOT NULL,
  `KmTarief` decimal(18,2) NOT NULL,
  `UurtariefWeekend` decimal(18,2) NOT NULL,
  `Secretariaatskosten` decimal(18,2) NOT NULL,
  `Aansluitkosten` decimal(18,2) NOT NULL,
  `DatumAansluitkosten` date DEFAULT NULL,
  `OmschrijvingFactuur` varchar(50) DEFAULT NULL,
  `Abonnement` decimal(18,2) NOT NULL,
  `SociaalbezoekTarief` decimal(18,2) NOT NULL,
  `StandaardHuisbezoekTarief` decimal(18,2) NOT NULL,
  `HuisbezoekTarief` decimal(18,2) NOT NULL,
  `SpoedbezoekTarief` decimal(18,2) NOT NULL,
  `SpoedbezoekZelfdedagTarief` decimal(18,2) NOT NULL,
  `HuisbezoekZaterdagTarief` decimal(18,2) NOT NULL,
  `TelefonischeControleTarief` decimal(18,2) NOT NULL,
  `AbonnementPeriode` int(11) DEFAULT NULL,
  `MaandbedragSecretariaat` decimal(18,2) NOT NULL,
  `AansluitkostenPeriode` int(11) NOT NULL,
  `Vasttariefhuisbezoeken` int(11) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `HD_IX` (`holdingId`),
  KEY `WG_IX` (`werkgeverId`)
) ENGINE=InnoDB AUTO_INCREMENT=380 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TODO`
--

DROP TABLE IF EXISTS `TODO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TODO` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activiteit_ID` int(11) NOT NULL,
  `verzuim_ID` int(11) NOT NULL,
  `verzuimactiviteit_ID` int(11) DEFAULT NULL,
  `user` int(11) NOT NULL,
  `soort` int(11) NOT NULL COMMENT 'handmatig of automatisch',
  `deadlinedatum` date NOT NULL,
  `waarschuwingsdatum` date NOT NULL,
  `herhalen` int(11) NOT NULL,
  `opmerking` varchar(255) DEFAULT NULL,
  `aanmaakdatum` datetime DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `verzuimix` (`verzuim_ID`),
  KEY `activiteitix` (`activiteit_ID`),
  KEY `verzuimactiviteitix` (`verzuimactiviteit_ID`),
  KEY `userix` (`user`)
) ENGINE=InnoDB AUTO_INCREMENT=438135 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UITVOERINGSINSTITUUT`
--

DROP TABLE IF EXISTS `UITVOERINGSINSTITUUT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UITVOERINGSINSTITUUT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(50) NOT NULL,
  `vestigingsadres_ID` int(11) NOT NULL,
  `postadres_ID` int(11) DEFAULT NULL,
  `telefoonnummer` varchar(20) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Uitkeringsinstantie_adres1` (`vestigingsadres_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERZUIM`
--

DROP TABLE IF EXISTS `VERZUIM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VERZUIM` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dienstverband_ID` int(11) NOT NULL,
  `startdatumverzuim` datetime NOT NULL,
  `einddatumverzuim` datetime DEFAULT NULL,
  `meldingsdatum` datetime NOT NULL,
  `gebruiker` int(11) NOT NULL,
  `cascode` int(11) NOT NULL,
  `ketenverzuim` int(11) NOT NULL,
  `vangnettype` int(11) NOT NULL,
  `gerelateerdheid` int(11) NOT NULL,
  `verzuimtype` int(11) NOT NULL,
  `opmerkingen` varchar(250) DEFAULT NULL,
  `meldingswijze` int(11) NOT NULL DEFAULT '0',
  `uitkeringnaarwerknemer` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_dienstverband_id` (`dienstverband_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=42035 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERZUIMACTIVITEIT`
--

DROP TABLE IF EXISTS `VERZUIMACTIVITEIT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VERZUIMACTIVITEIT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `verzuim_ID` int(11) NOT NULL,
  `activiteit_ID` int(11) NOT NULL,
  `datumactiviteit` datetime NOT NULL,
  `datumdeadline` date DEFAULT NULL,
  `opmerkingen` varchar(255) DEFAULT NULL,
  `tijdbesteed` int(11) DEFAULT NULL,
  `user` int(11) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `verzuimix` (`verzuim_ID`),
  KEY `activiteitix` (`activiteit_ID`),
  KEY `userix` (`user`)
) ENGINE=InnoDB AUTO_INCREMENT=73186 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERZUIMDOCUMENT`
--

DROP TABLE IF EXISTS `VERZUIMDOCUMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VERZUIMDOCUMENT` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `verzuim_id` int(11) NOT NULL,
  `documentnaam` varchar(100) NOT NULL,
  `padnaam` varchar(255) NOT NULL,
  `aanmaakdatum` datetime NOT NULL,
  `aanmaakuser` int(11) NOT NULL,
  `omschrijving` varchar(255) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ixverzuim` (`verzuim_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21970 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERZUIMHERSTEL`
--

DROP TABLE IF EXISTS `VERZUIMHERSTEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VERZUIMHERSTEL` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `verzuim_id` int(11) NOT NULL,
  `datum_herstel` datetime NOT NULL,
  `percentage_herstel` decimal(5,2) NOT NULL,
  `meldingsdatum` datetime NOT NULL,
  `user` int(11) NOT NULL,
  `opmerkingen` varchar(255) DEFAULT NULL,
  `percentage_herstel_at` decimal(5,2) NOT NULL DEFAULT '0.00',
  `meldingswijze` int(11) NOT NULL DEFAULT '0',
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_verzuimid` (`verzuim_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50458 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `VERZUIMMEDISCHEKAART`
--

DROP TABLE IF EXISTS `VERZUIMMEDISCHEKAART`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `VERZUIMMEDISCHEKAART` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `verzuim_ID` int(11) NOT NULL,
  `medischekaart` text NOT NULL,
  `wijzigingsdatum` datetime NOT NULL,
  `user` int(11) NOT NULL,
  `openbaar` int(11) NOT NULL DEFAULT '0' COMMENT 'Geeft aan of de gegevens zichtbaar zijn voor iedereen. ',
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `verzuimix` (`verzuim_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=70255 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `V_ACTUEELVERZUIM`
--

DROP TABLE IF EXISTS `V_ACTUEELVERZUIM`;
/*!50001 DROP VIEW IF EXISTS `V_ACTUEELVERZUIM`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `V_ACTUEELVERZUIM` AS SELECT 
 1 AS `einddatumcontract`,
 1 AS `startdatumcontract`,
 1 AS `dienstverbandid`,
 1 AS `werkweek`,
 1 AS `personeelsnummer`,
 1 AS `voorletters`,
 1 AS `voorvoegsel`,
 1 AS `achternaam`,
 1 AS `voornaam`,
 1 AS `werknemerid`,
 1 AS `geslacht`,
 1 AS `burgerservicenummer`,
 1 AS `geboortedatum`,
 1 AS `verzuimmeldingsdatum`,
 1 AS `einddatumverzuim`,
 1 AS `startdatumverzuim`,
 1 AS `gerelateerdheid`,
 1 AS `cascode`,
 1 AS `cascodeomschrijving`,
 1 AS `opmerkingen`,
 1 AS `verzuimid`,
 1 AS `vangnettype`,
 1 AS `werkgevernaam`,
 1 AS `werkgeverid`,
 1 AS `holdingid`,
 1 AS `werkgeverwerkweek`,
 1 AS `holdingnaam`,
 1 AS `Afdelingnaam`,
 1 AS `afdelingid`,
 1 AS `datum_herstel`,
 1 AS `percentage_herstel`,
 1 AS `percentage_herstel_at`,
 1 AS `herstelmeldingsdatum`,
 1 AS `herstelopmerkingen`,
 1 AS `herstelid`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `V_WERKNEMERVERZUIM`
--

DROP TABLE IF EXISTS `V_WERKNEMERVERZUIM`;
/*!50001 DROP VIEW IF EXISTS `V_WERKNEMERVERZUIM`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `V_WERKNEMERVERZUIM` AS SELECT 
 1 AS `einddatumcontract`,
 1 AS `startdatumcontract`,
 1 AS `dienstverbandid`,
 1 AS `werkweek`,
 1 AS `voorletters`,
 1 AS `achternaam`,
 1 AS `voornaam`,
 1 AS `voorvoegsel`,
 1 AS `werknemerid`,
 1 AS `geslacht`,
 1 AS `burgerservicenummer`,
 1 AS `geboortedatum`,
 1 AS `werkgevernaam`,
 1 AS `werkgeverid`,
 1 AS `holdingid`,
 1 AS `werkgeverwerkweek`,
 1 AS `holdingnaam`,
 1 AS `Afdelingnaam`,
 1 AS `afdelingid`,
 1 AS `einddatumverzuim`,
 1 AS `startdatumverzuim`,
 1 AS `aantalverzuimen`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `WERKGEVER`
--

DROP TABLE IF EXISTS `WERKGEVER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WERKGEVER` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `naam` varchar(45) DEFAULT NULL,
  `Holding_id` int(11) DEFAULT NULL,
  `startdatumcontract` date DEFAULT NULL,
  `einddatumcontract` date DEFAULT NULL,
  `vestigingsadres_ID` int(11) DEFAULT NULL,
  `postadres_ID` int(11) DEFAULT NULL,
  `factuuradres_ID` int(11) DEFAULT NULL,
  `telefoon` varchar(20) DEFAULT NULL,
  `contactpersoon_ID` int(11) DEFAULT NULL,
  `arbodienst_ID` int(11) DEFAULT NULL,
  `uitvoeringsinstituut_ID` int(11) DEFAULT NULL,
  `bedrijfsarts_ID` int(11) DEFAULT NULL,
  `werkweek` decimal(5,2) DEFAULT '40.00',
  `BTWnummer` varchar(45) DEFAULT NULL,
  `Debiteurnummer` int(11) DEFAULT NULL,
  `Detailsecretariaat` int(11) DEFAULT '0',
  `Emailadresfactuur` varchar(254) DEFAULT NULL,
  `Factureren` int(11) DEFAULT '0',
  `contactpersoonfactuur_ID` int(11) DEFAULT NULL,
  `externcontractnummer` varchar(45) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_werkgever_Holding1` (`Holding_id`),
  KEY `fk_werkgever_Adres1` (`vestigingsadres_ID`),
  KEY `fk_werkgever_contactpersoon1` (`contactpersoon_ID`),
  KEY `fk_arbodienst` (`arbodienst_ID`),
  KEY `fk_bedrijfsarts` (`bedrijfsarts_ID`),
  KEY `fk_uwv` (`uitvoeringsinstituut_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=417 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WERKGEVER_HAS_PAKKET`
--

DROP TABLE IF EXISTS `WERKGEVER_HAS_PAKKET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WERKGEVER_HAS_PAKKET` (
  `werkgever_ID` int(11) NOT NULL,
  `pakket_ID` int(11) NOT NULL,
  PRIMARY KEY (`werkgever_ID`,`pakket_ID`),
  KEY `fk_werkgever_has_pakket_pakket1` (`pakket_ID`),
  KEY `fk_werkgever_has_pakket_werkgever1` (`werkgever_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WERKNEMER`
--

DROP TABLE IF EXISTS `WERKNEMER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WERKNEMER` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `burgerservicenummer` varchar(11) DEFAULT NULL,
  `achternaam` varchar(50) DEFAULT NULL,
  `voornaam` varchar(20) DEFAULT NULL,
  `voorvoegsel` varchar(10) DEFAULT NULL,
  `geboortedatum` date DEFAULT NULL,
  `geslacht` int(11) DEFAULT NULL,
  `burgerlijkestaat` int(11) DEFAULT NULL,
  `telefoon` varchar(20) DEFAULT NULL,
  `mobiel` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `werkgever_ID` int(11) DEFAULT NULL,
  `adres_ID` int(11) DEFAULT NULL,
  `telefoonprive` varchar(20) DEFAULT NULL,
  `arbeidsgehandicapt` int(11) DEFAULT NULL,
  `opmerkingen` varchar(250) DEFAULT NULL,
  `voorletters` varchar(20) DEFAULT NULL,
  `wijzigingsdatum` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_werkgever` (`werkgever_ID`),
  KEY `ix_adres` (`adres_ID`),
  KEY `ix_achternaam` (`achternaam`)
) ENGINE=InnoDB AUTO_INCREMENT=36530 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WERKZAAMHEDEN`
--

DROP TABLE IF EXISTS `WERKZAAMHEDEN`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WERKZAAMHEDEN` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datum` datetime NOT NULL,
  `persoon` varchar(255) NOT NULL,
  `woonplaats` varchar(255) NOT NULL,
  `uren` decimal(18,2) NOT NULL,
  `aantalkm` decimal(18,2) NOT NULL,
  `overigekosten` decimal(18,2) NOT NULL,
  `werkgeverid` int(11) NOT NULL,
  `holdingId` int(11) DEFAULT NULL,
  `soortwerkzaamheden` int(11) NOT NULL,
  `personeelsnummer` char(10) DEFAULT NULL,
  `userid` int(11) NOT NULL,
  `geslacht` int(11) NOT NULL,
  `soortverzuim` int(11) NOT NULL,
  `filiaalid` int(11) DEFAULT NULL,
  `urgentie` int(11) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  `omschrijving` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ixdatum` (`datum`),
  KEY `ixuser` (`userid`),
  KEY `ixwerkgever` (`werkgeverid`,`datum`)
) ENGINE=InnoDB AUTO_INCREMENT=62393 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WIAPERCENTAGE`
--

DROP TABLE IF EXISTS `WIAPERCENTAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WIAPERCENTAGE` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `werknemer_ID` int(11) NOT NULL,
  `startdatum` date NOT NULL,
  `einddatum` date DEFAULT NULL,
  `code_wia_percentage` int(11) DEFAULT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `creationts` datetime DEFAULT NULL,
  `updatets` datetime DEFAULT NULL,
  `createdby` int(11) DEFAULT NULL,
  `updatedby` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ix_werknemer` (`werknemer_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=28104 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `V_ACTUEELVERZUIM`
--

/*!50001 DROP VIEW IF EXISTS `V_ACTUEELVERZUIM`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `V_ACTUEELVERZUIM` AS select `dienstverband`.`einddatumcontract` AS `einddatumcontract`,`dienstverband`.`startdatumcontract` AS `startdatumcontract`,`dienstverband`.`id` AS `dienstverbandid`,`dienstverband`.`werkweek` AS `werkweek`,`dienstverband`.`personeelsnummer` AS `personeelsnummer`,`werknemer`.`voorletters` AS `voorletters`,`werknemer`.`voorvoegsel` AS `voorvoegsel`,`werknemer`.`achternaam` AS `achternaam`,`werknemer`.`voornaam` AS `voornaam`,`werknemer`.`id` AS `werknemerid`,`werknemer`.`geslacht` AS `geslacht`,`werknemer`.`burgerservicenummer` AS `burgerservicenummer`,`werknemer`.`geboortedatum` AS `geboortedatum`,`verzuim`.`meldingsdatum` AS `verzuimmeldingsdatum`,`verzuim`.`einddatumverzuim` AS `einddatumverzuim`,`verzuim`.`startdatumverzuim` AS `startdatumverzuim`,`verzuim`.`gerelateerdheid` AS `gerelateerdheid`,`verzuim`.`cascode` AS `cascode`,`cascode`.`omschrijving` AS `cascodeomschrijving`,`verzuim`.`opmerkingen` AS `opmerkingen`,coalesce(`verzuim`.`id`,0) AS `verzuimid`,coalesce(`verzuim`.`vangnettype`,2) AS `vangnettype`,`werkgever`.`naam` AS `werkgevernaam`,`werkgever`.`id` AS `werkgeverid`,`werkgever`.`Holding_id` AS `holdingid`,`werkgever`.`werkweek` AS `werkgeverwerkweek`,`holding`.`naam` AS `holdingnaam`,`afdeling`.`naam` AS `Afdelingnaam`,`afdeling`.`id` AS `afdelingid`,`herstel`.`datum_herstel` AS `datum_herstel`,`herstel`.`percentage_herstel` AS `percentage_herstel`,`herstel`.`percentage_herstel_at` AS `percentage_herstel_at`,`herstel`.`meldingsdatum` AS `herstelmeldingsdatum`,`herstel`.`opmerkingen` AS `herstelopmerkingen`,coalesce(`herstel`.`id`,0) AS `herstelid` from ((((((((`WERKGEVER` `werkgever` left join `HOLDING` `holding` on((`werkgever`.`Holding_id` = `holding`.`id`))) join `AFDELING` `afdeling` on((`werkgever`.`id` = `afdeling`.`werkgever_id`))) join `AFDELING_HAS_WERKNEMER` `afdwnr` on((`afdeling`.`id` = `afdwnr`.`afdeling_ID`))) join `WERKNEMER` `werknemer` on((`werknemer`.`id` = `afdwnr`.`werknemer_ID`))) join `DIENSTVERBAND` `dienstverband` on((`dienstverband`.`werknemer_ID` = `werknemer`.`id`))) left join `VERZUIM` `verzuim` on((`verzuim`.`dienstverband_ID` = `dienstverband`.`id`))) left join `CASCODE` `cascode` on((`verzuim`.`cascode` = `cascode`.`id`))) left join `VERZUIMHERSTEL` `herstel` on((`herstel`.`verzuim_id` = `verzuim`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `V_WERKNEMERVERZUIM`
--

/*!50001 DROP VIEW IF EXISTS `V_WERKNEMERVERZUIM`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `V_WERKNEMERVERZUIM` AS select `dienstverband`.`einddatumcontract` AS `einddatumcontract`,`dienstverband`.`startdatumcontract` AS `startdatumcontract`,`dienstverband`.`id` AS `dienstverbandid`,`dienstverband`.`werkweek` AS `werkweek`,`werknemer`.`voorletters` AS `voorletters`,`werknemer`.`achternaam` AS `achternaam`,`werknemer`.`voornaam` AS `voornaam`,`werknemer`.`voorvoegsel` AS `voorvoegsel`,`werknemer`.`id` AS `werknemerid`,`werknemer`.`geslacht` AS `geslacht`,`werknemer`.`burgerservicenummer` AS `burgerservicenummer`,`werknemer`.`geboortedatum` AS `geboortedatum`,`werkgever`.`naam` AS `werkgevernaam`,`werkgever`.`id` AS `werkgeverid`,`werkgever`.`Holding_id` AS `holdingid`,`werkgever`.`werkweek` AS `werkgeverwerkweek`,`holding`.`naam` AS `holdingnaam`,`afdeling`.`naam` AS `Afdelingnaam`,`afdeling`.`id` AS `afdelingid`,`verzuim`.`einddatumverzuim` AS `einddatumverzuim`,`verzuim`.`startdatumverzuim` AS `startdatumverzuim`,count(0) AS `aantalverzuimen` from ((((((`WERKGEVER` `werkgever` left join `HOLDING` `holding` on((`werkgever`.`Holding_id` = `holding`.`id`))) join `AFDELING` `afdeling` on((`werkgever`.`id` = `afdeling`.`werkgever_id`))) join `AFDELING_HAS_WERKNEMER` `afdwnr` on((`afdeling`.`id` = `afdwnr`.`afdeling_ID`))) join `WERKNEMER` `werknemer` on((`werknemer`.`id` = `afdwnr`.`werknemer_ID`))) join `DIENSTVERBAND` `dienstverband` on((`dienstverband`.`werknemer_ID` = `werknemer`.`id`))) join `VERZUIM` `verzuim` on((`verzuim`.`dienstverband_ID` = `dienstverband`.`id`))) group by `werkgever`.`Holding_id`,`werkgever`.`id`,`werknemer`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-12-11 17:33:42
