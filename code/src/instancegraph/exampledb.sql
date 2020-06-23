-- phpMyAdmin SQL Dump
-- version 4.4.3
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Creato il: Ott 21, 2016 alle 11:29
-- Versione del server: 5.6.24
-- Versione PHP: 5.5.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `exampledb`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `alignedevent`
--

CREATE TABLE IF NOT EXISTS `alignedevent` (
  `posTrace` varchar(11) NOT NULL DEFAULT '',
  `class` varchar(500) NOT NULL DEFAULT '',
  `trace` varchar(11) NOT NULL DEFAULT '',
  `traceid` varchar(500) NOT NULL,
  `timestamp` varchar(100) DEFAULT NULL,
  `kind` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='eventwabototal';

-- --------------------------------------------------------

--
-- Struttura della tabella `alignments`
--

CREATE TABLE IF NOT EXISTS `alignments` (
  `traceId` varchar(100) NOT NULL,
  `alignment` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `anomalies`
--

CREATE TABLE IF NOT EXISTS `anomalies` (
  `id` int(11) NOT NULL,
  `posEvent` int(11) NOT NULL,
  `numTrace` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `eventclass` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `anomedges`
--

CREATE TABLE IF NOT EXISTS `anomedges` (
  `id` int(11) NOT NULL,
  `source` varchar(100) NOT NULL,
  `target` varchar(100) NOT NULL,
  `numTrace` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `causalrel`
--

CREATE TABLE IF NOT EXISTS `causalrel` (
  `source` varchar(500) NOT NULL,
  `target` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `causalreltau`
--

CREATE TABLE IF NOT EXISTS `causalreltau` (
  `id` int(11) NOT NULL,
  `source` varchar(500) NOT NULL,
  `target` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `covgraph`
--

CREATE TABLE IF NOT EXISTS `covgraph` (
  `id` int(11) NOT NULL,
  `sourceplace` varchar(500) NOT NULL,
  `transition` varchar(500) NOT NULL,
  `targetplace` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `disconnected`
--

CREATE TABLE IF NOT EXISTS `disconnected` (
  `numtrace` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura stand-in per le viste `discriminantfeat`
--
CREATE TABLE IF NOT EXISTS `discriminantfeat` (
`Sub11` varchar(100)
,`Sub67` varchar(100)
,`Sub72` varchar(100)
,`Sub75` varchar(100)
,`Sub96` varchar(100)
,`Sub101` varchar(100)
,`Sub113` varchar(100)
,`Sub125` varchar(100)
,`Sub129` varchar(100)
,`Sub163` varchar(100)
,`Sub169` varchar(100)
);

-- --------------------------------------------------------

--
-- Struttura della tabella `edge`
--

CREATE TABLE IF NOT EXISTS `edge` (
  `id` varchar(100) NOT NULL,
  `source` varchar(100) NOT NULL,
  `target` varchar(100) NOT NULL,
  `visited` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `edges`
--

CREATE TABLE IF NOT EXISTS `edges` (
  `id` int(11) NOT NULL,
  `id1` int(11) NOT NULL,
  `id2` int(11) NOT NULL,
  `label` varchar(400) NOT NULL,
  `dir` varchar(200) NOT NULL,
  `id_wf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `edgesbpinorep`
--

CREATE TABLE IF NOT EXISTS `edgesbpinorep` (
  `id` int(11) NOT NULL,
  `id1` int(11) NOT NULL,
  `id2` int(11) NOT NULL,
  `label` varchar(400) NOT NULL,
  `dir` varchar(200) NOT NULL,
  `id_wf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `event`
--

CREATE TABLE IF NOT EXISTS `event` (
  `posTrace` varchar(11) NOT NULL DEFAULT '',
  `class` varchar(500) NOT NULL DEFAULT '',
  `trace` varchar(11) NOT NULL DEFAULT '',
  `timestamp` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='eventwabototal';

-- --------------------------------------------------------

--
-- Struttura della tabella `nodes`
--

CREATE TABLE IF NOT EXISTS `nodes` (
  `pos` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `label` varchar(400) NOT NULL,
  `id_wf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `nodesbpinorep`
--

CREATE TABLE IF NOT EXISTS `nodesbpinorep` (
  `pos` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `label` varchar(400) NOT NULL,
  `id_wf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `place`
--

CREATE TABLE IF NOT EXISTS `place` (
  `id` varchar(100) NOT NULL,
  `name` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `startend`
--

CREATE TABLE IF NOT EXISTS `startend` (
  `event` varchar(200) NOT NULL,
  `type` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence` (
  `Id` int(100) unsigned NOT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL,
  `Sub84` varchar(100) DEFAULT NULL,
  `Sub85` varchar(100) DEFAULT NULL,
  `Sub86` varchar(100) DEFAULT NULL,
  `Sub87` varchar(100) DEFAULT NULL,
  `Sub88` varchar(100) DEFAULT NULL,
  `Sub89` varchar(100) DEFAULT NULL,
  `Sub90` varchar(100) DEFAULT NULL,
  `Sub91` varchar(100) DEFAULT NULL,
  `Sub92` varchar(100) DEFAULT NULL,
  `Sub93` varchar(100) DEFAULT NULL,
  `Sub94` varchar(100) DEFAULT NULL,
  `Sub95` varchar(100) DEFAULT NULL,
  `Sub96` varchar(100) DEFAULT NULL,
  `Sub97` varchar(100) DEFAULT NULL,
  `Sub98` varchar(100) DEFAULT NULL,
  `Sub99` varchar(100) DEFAULT NULL,
  `Sub100` varchar(100) DEFAULT NULL,
  `Sub101` varchar(100) DEFAULT NULL,
  `Sub102` varchar(100) DEFAULT NULL,
  `Sub103` varchar(100) DEFAULT NULL,
  `Sub104` varchar(100) DEFAULT NULL,
  `Sub105` varchar(100) DEFAULT NULL,
  `Sub106` varchar(100) DEFAULT NULL,
  `Sub107` varchar(100) DEFAULT NULL,
  `Sub108` varchar(100) DEFAULT NULL,
  `Sub109` varchar(100) DEFAULT NULL,
  `Sub110` varchar(100) DEFAULT NULL,
  `Sub111` varchar(100) DEFAULT NULL,
  `Sub112` varchar(100) DEFAULT NULL,
  `Sub113` varchar(100) DEFAULT NULL,
  `Sub114` varchar(100) DEFAULT NULL,
  `Sub115` varchar(100) DEFAULT NULL,
  `Sub116` varchar(100) DEFAULT NULL,
  `Sub117` varchar(100) DEFAULT NULL,
  `Sub118` varchar(100) DEFAULT NULL,
  `Sub119` varchar(100) DEFAULT NULL,
  `Sub120` varchar(100) DEFAULT NULL,
  `Sub121` varchar(100) DEFAULT NULL,
  `Sub122` varchar(100) DEFAULT NULL,
  `Sub123` varchar(100) DEFAULT NULL,
  `Sub124` varchar(100) DEFAULT NULL,
  `Sub125` varchar(100) DEFAULT NULL,
  `Sub126` varchar(100) DEFAULT NULL,
  `Sub127` varchar(100) DEFAULT NULL,
  `Sub128` varchar(100) DEFAULT NULL,
  `Sub129` varchar(100) DEFAULT NULL,
  `Sub130` varchar(100) DEFAULT NULL,
  `Sub131` varchar(100) DEFAULT NULL,
  `Sub132` varchar(100) DEFAULT NULL,
  `Sub133` varchar(100) DEFAULT NULL,
  `Sub134` varchar(100) DEFAULT NULL,
  `Sub135` varchar(100) DEFAULT NULL,
  `Sub136` varchar(100) DEFAULT NULL,
  `Sub137` varchar(100) DEFAULT NULL,
  `Sub138` varchar(100) DEFAULT NULL,
  `Sub139` varchar(100) DEFAULT NULL,
  `Sub140` varchar(100) DEFAULT NULL,
  `Sub141` varchar(100) DEFAULT NULL,
  `Sub142` varchar(100) DEFAULT NULL,
  `Sub143` varchar(100) DEFAULT NULL,
  `Sub144` varchar(100) DEFAULT NULL,
  `Sub145` varchar(100) DEFAULT NULL,
  `Sub146` varchar(100) DEFAULT NULL,
  `Sub147` varchar(100) DEFAULT NULL,
  `Sub148` varchar(100) DEFAULT NULL,
  `Sub149` varchar(100) DEFAULT NULL,
  `Sub150` varchar(100) DEFAULT NULL,
  `Sub151` varchar(100) DEFAULT NULL,
  `Sub152` varchar(100) DEFAULT NULL,
  `Sub153` varchar(100) DEFAULT NULL,
  `Sub154` varchar(100) DEFAULT NULL,
  `Sub155` varchar(100) DEFAULT NULL,
  `Sub156` varchar(100) DEFAULT NULL,
  `Sub157` varchar(100) DEFAULT NULL,
  `Sub158` varchar(100) DEFAULT NULL,
  `Sub159` varchar(100) DEFAULT NULL,
  `Sub160` varchar(100) DEFAULT NULL,
  `Sub161` varchar(100) DEFAULT NULL,
  `Sub162` varchar(100) DEFAULT NULL,
  `Sub163` varchar(100) DEFAULT NULL,
  `Sub164` varchar(100) DEFAULT NULL,
  `Sub165` varchar(100) DEFAULT NULL,
  `Sub166` varchar(100) DEFAULT NULL,
  `Sub167` varchar(100) DEFAULT NULL,
  `Sub168` varchar(100) DEFAULT NULL,
  `Sub169` varchar(100) DEFAULT NULL,
  `Sub170` varchar(100) DEFAULT NULL,
  `Sub171` varchar(100) DEFAULT NULL,
  `Sub172` varchar(100) DEFAULT NULL,
  `Sub173` varchar(100) DEFAULT NULL,
  `Sub174` varchar(100) DEFAULT NULL,
  `Sub175` varchar(100) DEFAULT NULL,
  `Sub176` varchar(100) DEFAULT NULL,
  `Sub177` varchar(100) DEFAULT NULL,
  `Sub178` varchar(100) DEFAULT NULL,
  `Sub179` varchar(100) DEFAULT NULL,
  `Sub180` varchar(100) DEFAULT NULL,
  `Sub181` varchar(100) DEFAULT NULL,
  `Sub182` varchar(100) DEFAULT NULL,
  `Sub183` varchar(100) DEFAULT NULL,
  `Sub184` varchar(100) DEFAULT NULL,
  `Sub185` varchar(100) DEFAULT NULL,
  `Sub186` varchar(100) DEFAULT NULL,
  `Sub187` varchar(100) DEFAULT NULL,
  `Sub188` varchar(100) DEFAULT NULL,
  `Sub189` varchar(100) DEFAULT NULL,
  `Sub190` varchar(100) DEFAULT NULL,
  `Sub191` varchar(100) DEFAULT NULL,
  `Sub192` varchar(100) DEFAULT NULL,
  `Sub193` varchar(100) DEFAULT NULL,
  `Sub194` varchar(100) DEFAULT NULL,
  `Sub195` varchar(100) DEFAULT NULL,
  `Sub196` varchar(100) DEFAULT NULL,
  `Sub197` varchar(100) DEFAULT NULL,
  `Sub198` varchar(100) DEFAULT NULL,
  `Sub199` varchar(100) DEFAULT NULL,
  `Sub200` varchar(100) DEFAULT NULL,
  `Sub201` varchar(100) DEFAULT NULL,
  `Sub202` varchar(100) DEFAULT NULL,
  `Sub203` varchar(100) DEFAULT NULL,
  `Sub204` varchar(100) DEFAULT NULL,
  `Sub205` varchar(100) DEFAULT NULL,
  `Sub206` varchar(100) DEFAULT NULL,
  `Sub207` varchar(100) DEFAULT NULL,
  `Sub208` varchar(100) DEFAULT NULL,
  `Sub209` varchar(100) DEFAULT NULL,
  `Sub210` varchar(100) DEFAULT NULL,
  `Sub211` varchar(100) DEFAULT NULL,
  `Sub212` varchar(100) DEFAULT NULL,
  `Sub213` varchar(100) DEFAULT NULL,
  `Sub214` varchar(100) DEFAULT NULL,
  `Sub215` varchar(100) DEFAULT NULL,
  `Sub216` varchar(100) DEFAULT NULL,
  `Sub217` varchar(100) DEFAULT NULL,
  `Sub218` varchar(100) DEFAULT NULL,
  `Sub219` varchar(100) DEFAULT NULL,
  `Sub220` varchar(100) DEFAULT NULL,
  `Sub221` varchar(100) DEFAULT NULL,
  `Sub222` varchar(100) DEFAULT NULL,
  `Sub223` varchar(100) DEFAULT NULL,
  `Sub224` varchar(100) DEFAULT NULL,
  `Sub225` varchar(100) DEFAULT NULL,
  `Sub226` varchar(100) DEFAULT NULL,
  `Sub227` varchar(100) DEFAULT NULL,
  `Sub228` varchar(100) DEFAULT NULL,
  `Sub229` varchar(100) DEFAULT NULL,
  `Sub230` varchar(100) DEFAULT NULL,
  `Sub231` varchar(100) DEFAULT NULL,
  `Sub232` varchar(100) DEFAULT NULL,
  `Sub233` varchar(100) DEFAULT NULL,
  `Sub234` varchar(100) DEFAULT NULL,
  `Sub235` varchar(100) DEFAULT NULL,
  `Sub236` varchar(100) DEFAULT NULL,
  `Sub237` varchar(100) DEFAULT NULL,
  `Sub238` varchar(100) DEFAULT NULL,
  `Sub239` varchar(100) DEFAULT NULL,
  `Sub240` varchar(100) DEFAULT NULL,
  `Sub241` varchar(100) DEFAULT NULL,
  `Sub242` varchar(100) DEFAULT NULL,
  `Sub243` varchar(100) DEFAULT NULL,
  `Sub244` varchar(100) DEFAULT NULL,
  `Sub245` varchar(100) DEFAULT NULL,
  `Sub246` varchar(100) DEFAULT NULL,
  `Sub247` varchar(100) DEFAULT NULL,
  `Sub248` varchar(100) DEFAULT NULL,
  `Sub249` varchar(100) DEFAULT NULL,
  `Sub250` varchar(100) DEFAULT NULL,
  `Sub251` varchar(100) DEFAULT NULL,
  `Sub252` varchar(100) DEFAULT NULL,
  `Sub253` varchar(100) DEFAULT NULL,
  `Sub254` varchar(100) DEFAULT NULL,
  `Sub255` varchar(100) DEFAULT NULL,
  `Sub256` varchar(100) DEFAULT NULL,
  `Sub257` varchar(100) DEFAULT NULL,
  `Sub258` varchar(100) DEFAULT NULL,
  `Sub259` varchar(100) DEFAULT NULL,
  `Sub260` varchar(100) DEFAULT NULL,
  `Sub261` varchar(100) DEFAULT NULL,
  `Sub262` varchar(100) DEFAULT NULL,
  `Sub263` varchar(100) DEFAULT NULL,
  `Sub264` varchar(100) DEFAULT NULL,
  `Sub265` varchar(100) DEFAULT NULL,
  `Sub266` varchar(100) DEFAULT NULL,
  `Sub267` varchar(100) DEFAULT NULL,
  `Sub268` varchar(100) DEFAULT NULL,
  `Sub269` varchar(100) DEFAULT NULL,
  `Sub270` varchar(100) DEFAULT NULL,
  `Sub271` varchar(100) DEFAULT NULL,
  `Sub272` varchar(100) DEFAULT NULL,
  `Sub273` varchar(100) DEFAULT NULL,
  `Sub274` varchar(100) DEFAULT NULL,
  `Sub275` varchar(100) DEFAULT NULL,
  `Sub276` varchar(100) DEFAULT NULL,
  `Sub277` varchar(100) DEFAULT NULL,
  `Sub278` varchar(100) DEFAULT NULL,
  `Sub279` varchar(100) DEFAULT NULL,
  `Sub280` varchar(100) DEFAULT NULL,
  `Sub281` varchar(100) DEFAULT NULL,
  `Sub282` varchar(100) DEFAULT NULL,
  `Sub283` varchar(100) DEFAULT NULL,
  `Sub284` varchar(100) DEFAULT NULL,
  `Sub285` varchar(100) DEFAULT NULL,
  `Sub286` varchar(100) DEFAULT NULL,
  `Sub287` varchar(100) DEFAULT NULL,
  `Sub288` varchar(100) DEFAULT NULL,
  `Sub289` varchar(100) DEFAULT NULL,
  `Sub290` varchar(100) DEFAULT NULL,
  `Sub291` varchar(100) DEFAULT NULL,
  `Sub292` varchar(100) DEFAULT NULL,
  `Sub293` varchar(100) DEFAULT NULL,
  `Sub294` varchar(100) DEFAULT NULL,
  `Sub295` varchar(100) DEFAULT NULL,
  `Sub296` varchar(100) DEFAULT NULL,
  `Sub297` varchar(100) DEFAULT NULL,
  `Sub298` varchar(100) DEFAULT NULL,
  `Sub299` varchar(100) DEFAULT NULL,
  `Sub300` varchar(100) DEFAULT NULL,
  `Sub301` varchar(100) DEFAULT NULL,
  `Sub302` varchar(100) DEFAULT NULL,
  `Sub303` varchar(100) DEFAULT NULL,
  `Sub304` varchar(100) DEFAULT NULL,
  `Sub305` varchar(100) DEFAULT NULL,
  `Sub306` varchar(100) DEFAULT NULL,
  `Sub307` varchar(100) DEFAULT NULL,
  `Sub308` varchar(100) DEFAULT NULL,
  `Sub309` varchar(100) DEFAULT NULL,
  `Sub310` varchar(100) DEFAULT NULL,
  `Sub311` varchar(100) DEFAULT NULL,
  `Sub312` varchar(100) DEFAULT NULL,
  `Sub313` varchar(100) DEFAULT NULL,
  `Sub314` varchar(100) DEFAULT NULL,
  `Sub315` varchar(100) DEFAULT NULL,
  `Sub316` varchar(100) DEFAULT NULL,
  `Sub317` varchar(100) DEFAULT NULL,
  `Sub318` varchar(100) DEFAULT NULL,
  `Sub319` varchar(100) DEFAULT NULL,
  `Sub320` varchar(100) DEFAULT NULL,
  `Sub321` varchar(100) DEFAULT NULL,
  `Sub322` varchar(100) DEFAULT NULL,
  `Sub323` varchar(100) DEFAULT NULL,
  `Sub324` varchar(100) DEFAULT NULL,
  `Sub325` varchar(100) DEFAULT NULL,
  `Sub326` varchar(100) DEFAULT NULL,
  `Sub327` varchar(100) DEFAULT NULL,
  `Sub328` varchar(100) DEFAULT NULL,
  `Sub329` varchar(100) DEFAULT NULL,
  `Sub330` varchar(100) DEFAULT NULL,
  `Sub331` varchar(100) DEFAULT NULL,
  `Sub332` varchar(100) DEFAULT NULL,
  `Sub333` varchar(100) DEFAULT NULL,
  `Sub334` varchar(100) DEFAULT NULL,
  `Sub335` varchar(100) DEFAULT NULL,
  `Sub336` varchar(100) DEFAULT NULL,
  `Sub337` varchar(100) DEFAULT NULL,
  `Sub338` varchar(100) DEFAULT NULL,
  `Sub339` varchar(100) DEFAULT NULL,
  `Sub340` varchar(100) DEFAULT NULL,
  `Sub341` varchar(100) DEFAULT NULL,
  `Sub342` varchar(100) DEFAULT NULL,
  `Sub343` varchar(100) DEFAULT NULL,
  `Sub344` varchar(100) DEFAULT NULL,
  `Sub345` varchar(100) DEFAULT NULL,
  `Sub346` varchar(100) DEFAULT NULL,
  `Sub347` varchar(100) DEFAULT NULL,
  `Sub348` varchar(100) DEFAULT NULL,
  `Sub349` varchar(100) DEFAULT NULL,
  `Sub350` varchar(100) DEFAULT NULL,
  `Sub351` varchar(100) DEFAULT NULL,
  `Sub352` varchar(100) DEFAULT NULL,
  `Sub353` varchar(100) DEFAULT NULL,
  `Sub354` varchar(100) DEFAULT NULL,
  `Sub355` varchar(100) DEFAULT NULL,
  `Sub356` varchar(100) DEFAULT NULL,
  `Sub357` varchar(100) DEFAULT NULL,
  `Sub358` varchar(100) DEFAULT NULL,
  `Sub359` varchar(100) DEFAULT NULL,
  `Sub360` varchar(100) DEFAULT NULL,
  `Sub361` varchar(100) DEFAULT NULL,
  `Sub362` varchar(100) DEFAULT NULL,
  `Sub363` varchar(100) DEFAULT NULL,
  `Sub364` varchar(100) DEFAULT NULL,
  `Sub365` varchar(100) DEFAULT NULL,
  `Sub366` varchar(100) DEFAULT NULL,
  `Sub367` varchar(100) DEFAULT NULL,
  `Sub368` varchar(100) DEFAULT NULL,
  `Sub369` varchar(100) DEFAULT NULL,
  `Sub370` varchar(100) DEFAULT NULL,
  `Sub371` varchar(100) DEFAULT NULL,
  `Sub372` varchar(100) DEFAULT NULL,
  `Sub373` varchar(100) DEFAULT NULL,
  `Sub374` varchar(100) DEFAULT NULL,
  `Sub375` varchar(100) DEFAULT NULL,
  `Sub376` varchar(100) DEFAULT NULL,
  `Sub377` varchar(100) DEFAULT NULL,
  `Sub378` varchar(100) DEFAULT NULL,
  `Sub379` varchar(100) DEFAULT NULL,
  `Sub380` varchar(100) DEFAULT NULL,
  `Sub381` varchar(100) DEFAULT NULL,
  `Sub382` varchar(100) DEFAULT NULL,
  `Sub383` varchar(100) DEFAULT NULL,
  `Sub384` varchar(100) DEFAULT NULL,
  `Sub385` varchar(100) DEFAULT NULL,
  `Sub386` varchar(100) DEFAULT NULL,
  `Sub387` varchar(100) DEFAULT NULL,
  `Sub388` varchar(100) DEFAULT NULL,
  `Sub389` varchar(100) DEFAULT NULL,
  `Sub390` varchar(100) DEFAULT NULL,
  `Sub391` varchar(100) DEFAULT NULL,
  `Sub392` varchar(100) DEFAULT NULL,
  `Sub393` varchar(100) DEFAULT NULL,
  `Sub394` varchar(100) DEFAULT NULL,
  `Sub395` varchar(100) DEFAULT NULL,
  `Sub396` varchar(100) DEFAULT NULL,
  `Sub397` varchar(100) DEFAULT NULL,
  `Sub398` varchar(100) DEFAULT NULL,
  `Sub399` varchar(100) DEFAULT NULL,
  `Sub400` varchar(100) DEFAULT NULL,
  `Sub401` varchar(100) DEFAULT NULL,
  `Sub402` varchar(100) DEFAULT NULL,
  `Sub403` varchar(100) DEFAULT NULL,
  `Sub404` varchar(100) DEFAULT NULL,
  `Sub405` varchar(100) DEFAULT NULL,
  `Sub406` varchar(100) DEFAULT NULL,
  `Sub407` varchar(100) DEFAULT NULL,
  `Sub408` varchar(100) DEFAULT NULL,
  `Sub409` varchar(100) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence_controlla`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence_controlla` (
  `Id` int(100) unsigned NOT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL,
  `Sub84` varchar(100) DEFAULT NULL,
  `Sub85` varchar(100) DEFAULT NULL,
  `Sub86` varchar(100) DEFAULT NULL,
  `Sub87` varchar(100) DEFAULT NULL,
  `Sub88` varchar(100) DEFAULT NULL,
  `Sub89` varchar(100) DEFAULT NULL,
  `Sub90` varchar(100) DEFAULT NULL,
  `Sub91` varchar(100) DEFAULT NULL,
  `Sub92` varchar(100) DEFAULT NULL,
  `Sub93` varchar(100) DEFAULT NULL,
  `Sub94` varchar(100) DEFAULT NULL,
  `Sub95` varchar(100) DEFAULT NULL,
  `Sub96` varchar(100) DEFAULT NULL,
  `Sub97` varchar(100) DEFAULT NULL,
  `Sub98` varchar(100) DEFAULT NULL,
  `Sub99` varchar(100) DEFAULT NULL,
  `Sub100` varchar(100) DEFAULT NULL,
  `Sub101` varchar(100) DEFAULT NULL,
  `Sub102` varchar(100) DEFAULT NULL,
  `Sub103` varchar(100) DEFAULT NULL,
  `Sub104` varchar(100) DEFAULT NULL,
  `Sub105` varchar(100) DEFAULT NULL,
  `Sub106` varchar(100) DEFAULT NULL,
  `Sub107` varchar(100) DEFAULT NULL,
  `Sub108` varchar(100) DEFAULT NULL,
  `Sub109` varchar(100) DEFAULT NULL,
  `Sub110` varchar(100) DEFAULT NULL,
  `Sub111` varchar(100) DEFAULT NULL,
  `Sub112` varchar(100) DEFAULT NULL,
  `Sub113` varchar(100) DEFAULT NULL,
  `Sub114` varchar(100) DEFAULT NULL,
  `Sub115` varchar(100) DEFAULT NULL,
  `Sub116` varchar(100) DEFAULT NULL,
  `Sub117` varchar(100) DEFAULT NULL,
  `Sub118` varchar(100) DEFAULT NULL,
  `Sub119` varchar(100) DEFAULT NULL,
  `Sub120` varchar(100) DEFAULT NULL,
  `Sub121` varchar(100) DEFAULT NULL,
  `Sub122` varchar(100) DEFAULT NULL,
  `Sub123` varchar(100) DEFAULT NULL,
  `Sub124` varchar(100) DEFAULT NULL,
  `Sub125` varchar(100) DEFAULT NULL,
  `Sub126` varchar(100) DEFAULT NULL,
  `Sub127` varchar(100) DEFAULT NULL,
  `Sub128` varchar(100) DEFAULT NULL,
  `Sub129` varchar(100) DEFAULT NULL,
  `Sub130` varchar(100) DEFAULT NULL,
  `Sub131` varchar(100) DEFAULT NULL,
  `Sub132` varchar(100) DEFAULT NULL,
  `Sub133` varchar(100) DEFAULT NULL,
  `Sub134` varchar(100) DEFAULT NULL,
  `Sub135` varchar(100) DEFAULT NULL,
  `Sub136` varchar(100) DEFAULT NULL,
  `Sub137` varchar(100) DEFAULT NULL,
  `Sub138` varchar(100) DEFAULT NULL,
  `Sub139` varchar(100) DEFAULT NULL,
  `Sub140` varchar(100) DEFAULT NULL,
  `Sub141` varchar(100) DEFAULT NULL,
  `Sub142` varchar(100) DEFAULT NULL,
  `Sub143` varchar(100) DEFAULT NULL,
  `Sub144` varchar(100) DEFAULT NULL,
  `Sub145` varchar(100) DEFAULT NULL,
  `Sub146` varchar(100) DEFAULT NULL,
  `Sub147` varchar(100) DEFAULT NULL,
  `Sub148` varchar(100) DEFAULT NULL,
  `Sub149` varchar(100) DEFAULT NULL,
  `Sub150` varchar(100) DEFAULT NULL,
  `Sub151` varchar(100) DEFAULT NULL,
  `Sub152` varchar(100) DEFAULT NULL,
  `Sub153` varchar(100) DEFAULT NULL,
  `Sub154` varchar(100) DEFAULT NULL,
  `Sub155` varchar(100) DEFAULT NULL,
  `Sub156` varchar(100) DEFAULT NULL,
  `Sub157` varchar(100) DEFAULT NULL,
  `Sub158` varchar(100) DEFAULT NULL,
  `Sub159` varchar(100) DEFAULT NULL,
  `Sub160` varchar(100) DEFAULT NULL,
  `Sub161` varchar(100) DEFAULT NULL,
  `Sub162` varchar(100) DEFAULT NULL,
  `Sub163` varchar(100) DEFAULT NULL,
  `Sub164` varchar(100) DEFAULT NULL,
  `Sub165` varchar(100) DEFAULT NULL,
  `Sub166` varchar(100) DEFAULT NULL,
  `Sub167` varchar(100) DEFAULT NULL,
  `Sub168` varchar(100) DEFAULT NULL,
  `Sub169` varchar(100) DEFAULT NULL,
  `Sub170` varchar(100) DEFAULT NULL,
  `Sub171` varchar(100) DEFAULT NULL,
  `Sub172` varchar(100) DEFAULT NULL,
  `Sub173` varchar(100) DEFAULT NULL,
  `Sub174` varchar(100) DEFAULT NULL,
  `Sub175` varchar(100) DEFAULT NULL,
  `Sub176` varchar(100) DEFAULT NULL,
  `Sub177` varchar(100) DEFAULT NULL,
  `Sub178` varchar(100) DEFAULT NULL,
  `Sub179` varchar(100) DEFAULT NULL,
  `Sub180` varchar(100) DEFAULT NULL,
  `Sub181` varchar(100) DEFAULT NULL,
  `Sub182` varchar(100) DEFAULT NULL,
  `Sub183` varchar(100) DEFAULT NULL,
  `Sub184` varchar(100) DEFAULT NULL,
  `Sub185` varchar(100) DEFAULT NULL,
  `Sub186` varchar(100) DEFAULT NULL,
  `Sub187` varchar(100) DEFAULT NULL,
  `Sub188` varchar(100) DEFAULT NULL,
  `Sub189` varchar(100) DEFAULT NULL,
  `Sub190` varchar(100) DEFAULT NULL,
  `Sub191` varchar(100) DEFAULT NULL,
  `Sub192` varchar(100) DEFAULT NULL,
  `Sub193` varchar(100) DEFAULT NULL,
  `Sub194` varchar(100) DEFAULT NULL,
  `Sub195` varchar(100) DEFAULT NULL,
  `Sub196` varchar(100) DEFAULT NULL,
  `Sub197` varchar(100) DEFAULT NULL,
  `Sub198` varchar(100) DEFAULT NULL,
  `Sub199` varchar(100) DEFAULT NULL,
  `Sub200` varchar(100) DEFAULT NULL,
  `Sub201` varchar(100) DEFAULT NULL,
  `Sub202` varchar(100) DEFAULT NULL,
  `Sub203` varchar(100) DEFAULT NULL,
  `Sub204` varchar(100) DEFAULT NULL,
  `Sub205` varchar(100) DEFAULT NULL,
  `Sub206` varchar(100) DEFAULT NULL,
  `Sub207` varchar(100) DEFAULT NULL,
  `Sub208` varchar(100) DEFAULT NULL,
  `Sub209` varchar(100) DEFAULT NULL,
  `Sub210` varchar(100) DEFAULT NULL,
  `Sub211` varchar(100) DEFAULT NULL,
  `Sub212` varchar(100) DEFAULT NULL,
  `Sub213` varchar(100) DEFAULT NULL,
  `Sub214` varchar(100) DEFAULT NULL,
  `Sub215` varchar(100) DEFAULT NULL,
  `Sub216` varchar(100) DEFAULT NULL,
  `Sub217` varchar(100) DEFAULT NULL,
  `Sub218` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence_hdcc_eswa`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence_hdcc_eswa` (
  `Id` int(100) unsigned NOT NULL,
  `Label` varchar(100) DEFAULT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL,
  `Sub84` varchar(100) DEFAULT NULL,
  `Sub85` varchar(100) DEFAULT NULL,
  `Sub86` varchar(100) DEFAULT NULL,
  `Sub87` varchar(100) DEFAULT NULL,
  `Sub88` varchar(100) DEFAULT NULL,
  `Sub89` varchar(100) DEFAULT NULL,
  `Sub90` varchar(100) DEFAULT NULL,
  `Sub91` varchar(100) DEFAULT NULL,
  `Sub92` varchar(100) DEFAULT NULL,
  `Sub93` varchar(100) DEFAULT NULL,
  `Sub94` varchar(100) DEFAULT NULL,
  `Sub95` varchar(100) DEFAULT NULL,
  `Sub96` varchar(100) DEFAULT NULL,
  `Sub97` varchar(100) DEFAULT NULL,
  `Sub98` varchar(100) DEFAULT NULL,
  `Sub99` varchar(100) DEFAULT NULL,
  `Sub100` varchar(100) DEFAULT NULL,
  `Sub101` varchar(100) DEFAULT NULL,
  `Sub102` varchar(100) DEFAULT NULL,
  `Sub103` varchar(100) DEFAULT NULL,
  `Sub104` varchar(100) DEFAULT NULL,
  `Sub105` varchar(100) DEFAULT NULL,
  `Sub106` varchar(100) DEFAULT NULL,
  `Sub107` varchar(100) DEFAULT NULL,
  `Sub108` varchar(100) DEFAULT NULL,
  `Sub109` varchar(100) DEFAULT NULL,
  `Sub110` varchar(100) DEFAULT NULL,
  `Sub111` varchar(100) DEFAULT NULL,
  `Sub112` varchar(100) DEFAULT NULL,
  `Sub113` varchar(100) DEFAULT NULL,
  `Sub114` varchar(100) DEFAULT NULL,
  `Sub115` varchar(100) DEFAULT NULL,
  `Sub116` varchar(100) DEFAULT NULL,
  `Sub117` varchar(100) DEFAULT NULL,
  `Sub118` varchar(100) DEFAULT NULL,
  `Sub119` varchar(100) DEFAULT NULL,
  `Sub120` varchar(100) DEFAULT NULL,
  `Sub121` varchar(100) DEFAULT NULL,
  `Sub122` varchar(100) DEFAULT NULL,
  `Sub123` varchar(100) DEFAULT NULL,
  `Sub124` varchar(100) DEFAULT NULL,
  `Sub125` varchar(100) DEFAULT NULL,
  `Sub126` varchar(100) DEFAULT NULL,
  `Sub127` varchar(100) DEFAULT NULL,
  `Sub128` varchar(100) DEFAULT NULL,
  `Sub129` varchar(100) DEFAULT NULL,
  `Sub130` varchar(100) DEFAULT NULL,
  `Sub131` varchar(100) DEFAULT NULL,
  `Sub132` varchar(100) DEFAULT NULL,
  `Sub133` varchar(100) DEFAULT NULL,
  `Sub134` varchar(100) DEFAULT NULL,
  `Sub135` varchar(100) DEFAULT NULL,
  `Sub136` varchar(100) DEFAULT NULL,
  `Sub137` varchar(100) DEFAULT NULL,
  `Sub138` varchar(100) DEFAULT NULL,
  `Sub139` varchar(100) DEFAULT NULL,
  `Sub140` varchar(100) DEFAULT NULL,
  `Sub141` varchar(100) DEFAULT NULL,
  `Sub142` varchar(100) DEFAULT NULL,
  `Sub143` varchar(100) DEFAULT NULL,
  `Sub144` varchar(100) DEFAULT NULL,
  `Sub145` varchar(100) DEFAULT NULL,
  `Sub146` varchar(100) DEFAULT NULL,
  `Sub147` varchar(100) DEFAULT NULL,
  `Sub148` varchar(100) DEFAULT NULL,
  `Sub149` varchar(100) DEFAULT NULL,
  `Sub150` varchar(100) DEFAULT NULL,
  `Sub151` varchar(100) DEFAULT NULL,
  `Sub152` varchar(100) DEFAULT NULL,
  `Sub153` varchar(100) DEFAULT NULL,
  `Sub154` varchar(100) DEFAULT NULL,
  `Sub155` varchar(100) DEFAULT NULL,
  `Sub156` varchar(100) DEFAULT NULL,
  `Sub157` varchar(100) DEFAULT NULL,
  `Sub158` varchar(100) DEFAULT NULL,
  `Sub159` varchar(100) DEFAULT NULL,
  `Sub160` varchar(100) DEFAULT NULL,
  `Sub161` varchar(100) DEFAULT NULL,
  `Sub162` varchar(100) DEFAULT NULL,
  `Sub163` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence_hdm13_eswa`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence_hdm13_eswa` (
  `Id` int(100) unsigned NOT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL,
  `Sub84` varchar(100) DEFAULT NULL,
  `Sub85` varchar(100) DEFAULT NULL,
  `Sub86` varchar(100) DEFAULT NULL,
  `Sub87` varchar(100) DEFAULT NULL,
  `Sub88` varchar(100) DEFAULT NULL,
  `Sub89` varchar(100) DEFAULT NULL,
  `Sub90` varchar(100) DEFAULT NULL,
  `Sub91` varchar(100) DEFAULT NULL,
  `Sub92` varchar(100) DEFAULT NULL,
  `Sub93` varchar(100) DEFAULT NULL,
  `Sub94` varchar(100) DEFAULT NULL,
  `Sub95` varchar(100) DEFAULT NULL,
  `Sub96` varchar(100) DEFAULT NULL,
  `Sub97` varchar(100) DEFAULT NULL,
  `Sub98` varchar(100) DEFAULT NULL,
  `Sub99` varchar(100) DEFAULT NULL,
  `Sub100` varchar(100) DEFAULT NULL,
  `Sub101` varchar(100) DEFAULT NULL,
  `Sub102` varchar(100) DEFAULT NULL,
  `Sub103` varchar(100) DEFAULT NULL,
  `Sub104` varchar(100) DEFAULT NULL,
  `Sub105` varchar(100) DEFAULT NULL,
  `Sub106` varchar(100) DEFAULT NULL,
  `Sub107` varchar(100) DEFAULT NULL,
  `Sub108` varchar(100) DEFAULT NULL,
  `Sub109` varchar(100) DEFAULT NULL,
  `Sub110` varchar(100) DEFAULT NULL,
  `Sub111` varchar(100) DEFAULT NULL,
  `Sub112` varchar(100) DEFAULT NULL,
  `Sub113` varchar(100) DEFAULT NULL,
  `Sub114` varchar(100) DEFAULT NULL,
  `Sub115` varchar(100) DEFAULT NULL,
  `Sub116` varchar(100) DEFAULT NULL,
  `Sub117` varchar(100) DEFAULT NULL,
  `Sub118` varchar(100) DEFAULT NULL,
  `Sub119` varchar(100) DEFAULT NULL,
  `Sub120` varchar(100) DEFAULT NULL,
  `Sub121` varchar(100) DEFAULT NULL,
  `Sub122` varchar(100) DEFAULT NULL,
  `Sub123` varchar(100) DEFAULT NULL,
  `Sub124` varchar(100) DEFAULT NULL,
  `Sub125` varchar(100) DEFAULT NULL,
  `Sub126` varchar(100) DEFAULT NULL,
  `Sub127` varchar(100) DEFAULT NULL,
  `Sub128` varchar(100) DEFAULT NULL,
  `Sub129` varchar(100) DEFAULT NULL,
  `Sub130` varchar(100) DEFAULT NULL,
  `Sub131` varchar(100) DEFAULT NULL,
  `Sub132` varchar(100) DEFAULT NULL,
  `Sub133` varchar(100) DEFAULT NULL,
  `Sub134` varchar(100) DEFAULT NULL,
  `Sub135` varchar(100) DEFAULT NULL,
  `Sub136` varchar(100) DEFAULT NULL,
  `Sub137` varchar(100) DEFAULT NULL,
  `Sub138` varchar(100) DEFAULT NULL,
  `Sub139` varchar(100) DEFAULT NULL,
  `Sub140` varchar(100) DEFAULT NULL,
  `Sub141` varchar(100) DEFAULT NULL,
  `Sub142` varchar(100) DEFAULT NULL,
  `Sub143` varchar(100) DEFAULT NULL,
  `Sub144` varchar(100) DEFAULT NULL,
  `Sub145` varchar(100) DEFAULT NULL,
  `Sub146` varchar(100) DEFAULT NULL,
  `Sub147` varchar(100) DEFAULT NULL,
  `Sub148` varchar(100) DEFAULT NULL,
  `Sub149` varchar(100) DEFAULT NULL,
  `Sub150` varchar(100) DEFAULT NULL,
  `Sub151` varchar(100) DEFAULT NULL,
  `Sub152` varchar(100) DEFAULT NULL,
  `Sub153` varchar(100) DEFAULT NULL,
  `Sub154` varchar(100) DEFAULT NULL,
  `Sub155` varchar(100) DEFAULT NULL,
  `Sub156` varchar(100) DEFAULT NULL,
  `Sub157` varchar(100) DEFAULT NULL,
  `Sub158` varchar(100) DEFAULT NULL,
  `Sub159` varchar(100) DEFAULT NULL,
  `Sub160` varchar(100) DEFAULT NULL,
  `Sub161` varchar(100) DEFAULT NULL,
  `Sub162` varchar(100) DEFAULT NULL,
  `Sub163` varchar(100) DEFAULT NULL,
  `Sub164` varchar(100) DEFAULT NULL,
  `Sub165` varchar(100) DEFAULT NULL,
  `Sub166` varchar(100) DEFAULT NULL,
  `Sub167` varchar(100) DEFAULT NULL,
  `Sub168` varchar(100) DEFAULT NULL,
  `Sub169` varchar(100) DEFAULT NULL,
  `Sub170` varchar(100) DEFAULT NULL,
  `Sub171` varchar(100) DEFAULT NULL,
  `Sub172` varchar(100) DEFAULT NULL,
  `Sub173` varchar(100) DEFAULT NULL,
  `Sub174` varchar(100) DEFAULT NULL,
  `Sub175` varchar(100) DEFAULT NULL,
  `Sub176` varchar(100) DEFAULT NULL,
  `Sub177` varchar(100) DEFAULT NULL,
  `Sub178` varchar(100) DEFAULT NULL,
  `Sub179` varchar(100) DEFAULT NULL,
  `Sub180` varchar(100) DEFAULT NULL,
  `Sub181` varchar(100) DEFAULT NULL,
  `Sub182` varchar(100) DEFAULT NULL,
  `Sub183` varchar(100) DEFAULT NULL,
  `Sub184` varchar(100) DEFAULT NULL,
  `Sub185` varchar(100) DEFAULT NULL,
  `Sub186` varchar(100) DEFAULT NULL,
  `Sub187` varchar(100) DEFAULT NULL,
  `Sub188` varchar(100) DEFAULT NULL,
  `Sub189` varchar(100) DEFAULT NULL,
  `Sub190` varchar(100) DEFAULT NULL,
  `Sub191` varchar(100) DEFAULT NULL,
  `Sub192` varchar(100) DEFAULT NULL,
  `Sub193` varchar(100) DEFAULT NULL,
  `Sub194` varchar(100) DEFAULT NULL,
  `Sub195` varchar(100) DEFAULT NULL,
  `Sub196` varchar(100) DEFAULT NULL,
  `Sub197` varchar(100) DEFAULT NULL,
  `Sub198` varchar(100) DEFAULT NULL,
  `Sub199` varchar(100) DEFAULT NULL,
  `Sub200` varchar(100) DEFAULT NULL,
  `Sub201` varchar(100) DEFAULT NULL,
  `Sub202` varchar(100) DEFAULT NULL,
  `Sub203` varchar(100) DEFAULT NULL,
  `Sub204` varchar(100) DEFAULT NULL,
  `Sub205` varchar(100) DEFAULT NULL,
  `Sub206` varchar(100) DEFAULT NULL,
  `Sub207` varchar(100) DEFAULT NULL,
  `Sub208` varchar(100) DEFAULT NULL,
  `Sub209` varchar(100) DEFAULT NULL,
  `Sub210` varchar(100) DEFAULT NULL,
  `Sub211` varchar(100) DEFAULT NULL,
  `Sub212` varchar(100) DEFAULT NULL,
  `Sub213` varchar(100) DEFAULT NULL,
  `Sub214` varchar(100) DEFAULT NULL,
  `Sub215` varchar(100) DEFAULT NULL,
  `Sub216` varchar(100) DEFAULT NULL,
  `Sub217` varchar(100) DEFAULT NULL,
  `Sub218` varchar(100) DEFAULT NULL,
  `Sub219` varchar(100) DEFAULT NULL,
  `Sub220` varchar(100) DEFAULT NULL,
  `Sub221` varchar(100) DEFAULT NULL,
  `Sub222` varchar(100) DEFAULT NULL,
  `Sub223` varchar(100) DEFAULT NULL,
  `Sub224` varchar(100) DEFAULT NULL,
  `Sub225` varchar(100) DEFAULT NULL,
  `Sub226` varchar(100) DEFAULT NULL,
  `Sub227` varchar(100) DEFAULT NULL,
  `Sub228` varchar(100) DEFAULT NULL,
  `Sub229` varchar(100) DEFAULT NULL,
  `Sub230` varchar(100) DEFAULT NULL,
  `Sub231` varchar(100) DEFAULT NULL,
  `Sub232` varchar(100) DEFAULT NULL,
  `Sub233` varchar(100) DEFAULT NULL,
  `Sub234` varchar(100) DEFAULT NULL,
  `Sub235` varchar(100) DEFAULT NULL,
  `Sub236` varchar(100) DEFAULT NULL,
  `Sub237` varchar(100) DEFAULT NULL,
  `Sub238` varchar(100) DEFAULT NULL,
  `Sub239` varchar(100) DEFAULT NULL,
  `Sub240` varchar(100) DEFAULT NULL,
  `Sub241` varchar(100) DEFAULT NULL,
  `Sub242` varchar(100) DEFAULT NULL,
  `Sub243` varchar(100) DEFAULT NULL,
  `Sub244` varchar(100) DEFAULT NULL,
  `Sub245` varchar(100) DEFAULT NULL,
  `Sub246` varchar(100) DEFAULT NULL,
  `Sub247` varchar(100) DEFAULT NULL,
  `Sub248` varchar(100) DEFAULT NULL,
  `Sub249` varchar(100) DEFAULT NULL,
  `Sub250` varchar(100) DEFAULT NULL,
  `Sub251` varchar(100) DEFAULT NULL,
  `Sub252` varchar(100) DEFAULT NULL,
  `Sub253` varchar(100) DEFAULT NULL,
  `Sub254` varchar(100) DEFAULT NULL,
  `Sub255` varchar(100) DEFAULT NULL,
  `Sub256` varchar(100) DEFAULT NULL,
  `Sub257` varchar(100) DEFAULT NULL,
  `Sub258` varchar(100) DEFAULT NULL,
  `Sub259` varchar(100) DEFAULT NULL,
  `Sub260` varchar(100) DEFAULT NULL,
  `Sub261` varchar(100) DEFAULT NULL,
  `Sub262` varchar(100) DEFAULT NULL,
  `Sub263` varchar(100) DEFAULT NULL,
  `Sub264` varchar(100) DEFAULT NULL,
  `Sub265` varchar(100) DEFAULT NULL,
  `Sub266` varchar(100) DEFAULT NULL,
  `Sub267` varchar(100) DEFAULT NULL,
  `Sub268` varchar(100) DEFAULT NULL,
  `Sub269` varchar(100) DEFAULT NULL,
  `Sub270` varchar(100) DEFAULT NULL,
  `Sub271` varchar(100) DEFAULT NULL,
  `Sub272` varchar(100) DEFAULT NULL,
  `Sub273` varchar(100) DEFAULT NULL,
  `Sub274` varchar(100) DEFAULT NULL,
  `Sub275` varchar(100) DEFAULT NULL,
  `Sub276` varchar(100) DEFAULT NULL,
  `Sub277` varchar(100) DEFAULT NULL,
  `Sub278` varchar(100) DEFAULT NULL,
  `Sub279` varchar(100) DEFAULT NULL,
  `Sub280` varchar(100) DEFAULT NULL,
  `Sub281` varchar(100) DEFAULT NULL,
  `Sub282` varchar(100) DEFAULT NULL,
  `Sub283` varchar(100) DEFAULT NULL,
  `Sub284` varchar(100) DEFAULT NULL,
  `Sub285` varchar(100) DEFAULT NULL,
  `Sub286` varchar(100) DEFAULT NULL,
  `Sub287` varchar(100) DEFAULT NULL,
  `Sub288` varchar(100) DEFAULT NULL,
  `Sub289` varchar(100) DEFAULT NULL,
  `Sub290` varchar(100) DEFAULT NULL,
  `Sub291` varchar(100) DEFAULT NULL,
  `Sub292` varchar(100) DEFAULT NULL,
  `Sub293` varchar(100) DEFAULT NULL,
  `Sub294` varchar(100) DEFAULT NULL,
  `Sub295` varchar(100) DEFAULT NULL,
  `Sub296` varchar(100) DEFAULT NULL,
  `Sub297` varchar(100) DEFAULT NULL,
  `Sub298` varchar(100) DEFAULT NULL,
  `Sub299` varchar(100) DEFAULT NULL,
  `Sub300` varchar(100) DEFAULT NULL,
  `Sub301` varchar(100) DEFAULT NULL,
  `Sub302` varchar(100) DEFAULT NULL,
  `Sub303` varchar(100) DEFAULT NULL,
  `Sub304` varchar(100) DEFAULT NULL,
  `Sub305` varchar(100) DEFAULT NULL,
  `Sub306` varchar(100) DEFAULT NULL,
  `Sub307` varchar(100) DEFAULT NULL,
  `Sub308` varchar(100) DEFAULT NULL,
  `Sub309` varchar(100) DEFAULT NULL,
  `Sub310` varchar(100) DEFAULT NULL,
  `Sub311` varchar(100) DEFAULT NULL,
  `Sub312` varchar(100) DEFAULT NULL,
  `Sub313` varchar(100) DEFAULT NULL,
  `Sub314` varchar(100) DEFAULT NULL,
  `Sub315` varchar(100) DEFAULT NULL,
  `Sub316` varchar(100) DEFAULT NULL,
  `Sub317` varchar(100) DEFAULT NULL,
  `Sub318` varchar(100) DEFAULT NULL,
  `Sub319` varchar(100) DEFAULT NULL,
  `Sub320` varchar(100) DEFAULT NULL,
  `Sub321` varchar(100) DEFAULT NULL,
  `Sub322` varchar(100) DEFAULT NULL,
  `Sub323` varchar(100) DEFAULT NULL,
  `Sub324` varchar(100) DEFAULT NULL,
  `Sub325` varchar(100) DEFAULT NULL,
  `Sub326` varchar(100) DEFAULT NULL,
  `Sub327` varchar(100) DEFAULT NULL,
  `Sub328` varchar(100) DEFAULT NULL,
  `Sub329` varchar(100) DEFAULT NULL,
  `Sub330` varchar(100) DEFAULT NULL,
  `Sub331` varchar(100) DEFAULT NULL,
  `Sub332` varchar(100) DEFAULT NULL,
  `Sub333` varchar(100) DEFAULT NULL,
  `Sub334` varchar(100) DEFAULT NULL,
  `Sub335` varchar(100) DEFAULT NULL,
  `Sub336` varchar(100) DEFAULT NULL,
  `Sub337` varchar(100) DEFAULT NULL,
  `Sub338` varchar(100) DEFAULT NULL,
  `Sub339` varchar(100) DEFAULT NULL,
  `Sub340` varchar(100) DEFAULT NULL,
  `Sub341` varchar(100) DEFAULT NULL,
  `Sub342` varchar(100) DEFAULT NULL,
  `Sub343` varchar(100) DEFAULT NULL,
  `Sub344` varchar(100) DEFAULT NULL,
  `Sub345` varchar(100) DEFAULT NULL,
  `Sub346` varchar(100) DEFAULT NULL,
  `Sub347` varchar(100) DEFAULT NULL,
  `Sub348` varchar(100) DEFAULT NULL,
  `Sub349` varchar(100) DEFAULT NULL,
  `Sub350` varchar(100) DEFAULT NULL,
  `Sub351` varchar(100) DEFAULT NULL,
  `Sub352` varchar(100) DEFAULT NULL,
  `Sub353` varchar(100) DEFAULT NULL,
  `Sub354` varchar(100) DEFAULT NULL,
  `Sub355` varchar(100) DEFAULT NULL,
  `Sub356` varchar(100) DEFAULT NULL,
  `Sub357` varchar(100) DEFAULT NULL,
  `Sub358` varchar(100) DEFAULT NULL,
  `Sub359` varchar(100) DEFAULT NULL,
  `Sub360` varchar(100) DEFAULT NULL,
  `Sub361` varchar(100) DEFAULT NULL,
  `Sub362` varchar(100) DEFAULT NULL,
  `Sub363` varchar(100) DEFAULT NULL,
  `Sub364` varchar(100) DEFAULT NULL,
  `Sub365` varchar(100) DEFAULT NULL,
  `Sub366` varchar(100) DEFAULT NULL,
  `Sub367` varchar(100) DEFAULT NULL,
  `Sub368` varchar(100) DEFAULT NULL,
  `Sub369` varchar(100) DEFAULT NULL,
  `Sub370` varchar(100) DEFAULT NULL,
  `Sub371` varchar(100) DEFAULT NULL,
  `Sub372` varchar(100) DEFAULT NULL,
  `Sub373` varchar(100) DEFAULT NULL,
  `Sub374` varchar(100) DEFAULT NULL,
  `Sub375` varchar(100) DEFAULT NULL,
  `Sub376` varchar(100) DEFAULT NULL,
  `Sub377` varchar(100) DEFAULT NULL,
  `Sub378` varchar(100) DEFAULT NULL,
  `Sub379` varchar(100) DEFAULT NULL,
  `Sub380` varchar(100) DEFAULT NULL,
  `Sub381` varchar(100) DEFAULT NULL,
  `Sub382` varchar(100) DEFAULT NULL,
  `Sub383` varchar(100) DEFAULT NULL,
  `Sub384` varchar(100) DEFAULT NULL,
  `Sub385` varchar(100) DEFAULT NULL,
  `Sub386` varchar(100) DEFAULT NULL,
  `Sub387` varchar(100) DEFAULT NULL,
  `Sub388` varchar(100) DEFAULT NULL,
  `Sub389` varchar(100) DEFAULT NULL,
  `Sub390` varchar(100) DEFAULT NULL,
  `Sub391` varchar(100) DEFAULT NULL,
  `Sub392` varchar(100) DEFAULT NULL,
  `Sub393` varchar(100) DEFAULT NULL,
  `Sub394` varchar(100) DEFAULT NULL,
  `Sub395` varchar(100) DEFAULT NULL,
  `Sub396` varchar(100) DEFAULT NULL,
  `Sub397` varchar(100) DEFAULT NULL,
  `Sub398` varchar(100) DEFAULT NULL,
  `Sub399` varchar(100) DEFAULT NULL,
  `Sub400` varchar(100) DEFAULT NULL,
  `Sub401` varchar(100) DEFAULT NULL,
  `Sub402` varchar(100) DEFAULT NULL,
  `Sub403` varchar(100) DEFAULT NULL,
  `Sub404` varchar(100) DEFAULT NULL,
  `Sub405` varchar(100) DEFAULT NULL,
  `Sub406` varchar(100) DEFAULT NULL,
  `Sub407` varchar(100) DEFAULT NULL,
  `Sub408` varchar(100) DEFAULT NULL,
  `Sub409` varchar(100) DEFAULT NULL,
  `Sub410` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence_hdm13_eswa_new`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence_hdm13_eswa_new` (
  `Id` int(100) unsigned NOT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence_hdm16_eswa`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence_hdm16_eswa` (
  `Id` int(100) unsigned NOT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL,
  `Sub84` varchar(100) DEFAULT NULL,
  `Sub85` varchar(100) DEFAULT NULL,
  `Sub86` varchar(100) DEFAULT NULL,
  `Sub87` varchar(100) DEFAULT NULL,
  `Sub88` varchar(100) DEFAULT NULL,
  `Sub89` varchar(100) DEFAULT NULL,
  `Sub90` varchar(100) DEFAULT NULL,
  `Sub91` varchar(100) DEFAULT NULL,
  `Sub92` varchar(100) DEFAULT NULL,
  `Sub93` varchar(100) DEFAULT NULL,
  `Sub94` varchar(100) DEFAULT NULL,
  `Sub95` varchar(100) DEFAULT NULL,
  `Sub96` varchar(100) DEFAULT NULL,
  `Sub97` varchar(100) DEFAULT NULL,
  `Sub98` varchar(100) DEFAULT NULL,
  `Sub99` varchar(100) DEFAULT NULL,
  `Sub100` varchar(100) DEFAULT NULL,
  `Sub101` varchar(100) DEFAULT NULL,
  `Sub102` varchar(100) DEFAULT NULL,
  `Sub103` varchar(100) DEFAULT NULL,
  `Sub104` varchar(100) DEFAULT NULL,
  `Sub105` varchar(100) DEFAULT NULL,
  `Sub106` varchar(100) DEFAULT NULL,
  `Sub107` varchar(100) DEFAULT NULL,
  `Sub108` varchar(100) DEFAULT NULL,
  `Sub109` varchar(100) DEFAULT NULL,
  `Sub110` varchar(100) DEFAULT NULL,
  `Sub111` varchar(100) DEFAULT NULL,
  `Sub112` varchar(100) DEFAULT NULL,
  `Sub113` varchar(100) DEFAULT NULL,
  `Sub114` varchar(100) DEFAULT NULL,
  `Sub115` varchar(100) DEFAULT NULL,
  `Sub116` varchar(100) DEFAULT NULL,
  `Sub117` varchar(100) DEFAULT NULL,
  `Sub118` varchar(100) DEFAULT NULL,
  `Sub119` varchar(100) DEFAULT NULL,
  `Sub120` varchar(100) DEFAULT NULL,
  `Sub121` varchar(100) DEFAULT NULL,
  `Sub122` varchar(100) DEFAULT NULL,
  `Sub123` varchar(100) DEFAULT NULL,
  `Sub124` varchar(100) DEFAULT NULL,
  `Sub125` varchar(100) DEFAULT NULL,
  `Sub126` varchar(100) DEFAULT NULL,
  `Sub127` varchar(100) DEFAULT NULL,
  `Sub128` varchar(100) DEFAULT NULL,
  `Sub129` varchar(100) DEFAULT NULL,
  `Sub130` varchar(100) DEFAULT NULL,
  `Sub131` varchar(100) DEFAULT NULL,
  `Sub132` varchar(100) DEFAULT NULL,
  `Sub133` varchar(100) DEFAULT NULL,
  `Sub134` varchar(100) DEFAULT NULL,
  `Sub135` varchar(100) DEFAULT NULL,
  `Sub136` varchar(100) DEFAULT NULL,
  `Sub137` varchar(100) DEFAULT NULL,
  `Sub138` varchar(100) DEFAULT NULL,
  `Sub139` varchar(100) DEFAULT NULL,
  `Sub140` varchar(100) DEFAULT NULL,
  `Sub141` varchar(100) DEFAULT NULL,
  `Sub142` varchar(100) DEFAULT NULL,
  `Sub143` varchar(100) DEFAULT NULL,
  `Sub144` varchar(100) DEFAULT NULL,
  `Sub145` varchar(100) DEFAULT NULL,
  `Sub146` varchar(100) DEFAULT NULL,
  `Sub147` varchar(100) DEFAULT NULL,
  `Sub148` varchar(100) DEFAULT NULL,
  `Sub149` varchar(100) DEFAULT NULL,
  `Sub150` varchar(100) DEFAULT NULL,
  `Sub151` varchar(100) DEFAULT NULL,
  `Sub152` varchar(100) DEFAULT NULL,
  `Sub153` varchar(100) DEFAULT NULL,
  `Sub154` varchar(100) DEFAULT NULL,
  `Sub155` varchar(100) DEFAULT NULL,
  `Sub156` varchar(100) DEFAULT NULL,
  `Sub157` varchar(100) DEFAULT NULL,
  `Sub158` varchar(100) DEFAULT NULL,
  `Sub159` varchar(100) DEFAULT NULL,
  `Sub160` varchar(100) DEFAULT NULL,
  `Sub161` varchar(100) DEFAULT NULL,
  `Sub162` varchar(100) DEFAULT NULL,
  `Sub163` varchar(100) DEFAULT NULL,
  `Sub164` varchar(100) DEFAULT NULL,
  `Sub165` varchar(100) DEFAULT NULL,
  `Sub166` varchar(100) DEFAULT NULL,
  `Sub167` varchar(100) DEFAULT NULL,
  `Sub168` varchar(100) DEFAULT NULL,
  `Sub169` varchar(100) DEFAULT NULL,
  `Sub170` varchar(100) DEFAULT NULL,
  `Sub171` varchar(100) DEFAULT NULL,
  `Sub172` varchar(100) DEFAULT NULL,
  `Sub173` varchar(100) DEFAULT NULL,
  `Sub174` varchar(100) DEFAULT NULL,
  `Sub175` varchar(100) DEFAULT NULL,
  `Sub176` varchar(100) DEFAULT NULL,
  `Sub177` varchar(100) DEFAULT NULL,
  `Sub178` varchar(100) DEFAULT NULL,
  `Sub179` varchar(100) DEFAULT NULL,
  `Sub180` varchar(100) DEFAULT NULL,
  `Sub181` varchar(100) DEFAULT NULL,
  `Sub182` varchar(100) DEFAULT NULL,
  `Sub183` varchar(100) DEFAULT NULL,
  `Sub184` varchar(100) DEFAULT NULL,
  `Sub185` varchar(100) DEFAULT NULL,
  `Sub186` varchar(100) DEFAULT NULL,
  `Sub187` varchar(100) DEFAULT NULL,
  `Sub188` varchar(100) DEFAULT NULL,
  `Sub189` varchar(100) DEFAULT NULL,
  `Sub190` varchar(100) DEFAULT NULL,
  `Sub191` varchar(100) DEFAULT NULL,
  `Sub192` varchar(100) DEFAULT NULL,
  `Sub193` varchar(100) DEFAULT NULL,
  `Sub194` varchar(100) DEFAULT NULL,
  `Sub195` varchar(100) DEFAULT NULL,
  `Sub196` varchar(100) DEFAULT NULL,
  `Sub197` varchar(100) DEFAULT NULL,
  `Sub198` varchar(100) DEFAULT NULL,
  `Sub199` varchar(100) DEFAULT NULL,
  `Sub200` varchar(100) DEFAULT NULL,
  `Sub201` varchar(100) DEFAULT NULL,
  `Sub202` varchar(100) DEFAULT NULL,
  `Sub203` varchar(100) DEFAULT NULL,
  `Sub204` varchar(100) DEFAULT NULL,
  `Sub205` varchar(100) DEFAULT NULL,
  `Sub206` varchar(100) DEFAULT NULL,
  `Sub207` varchar(100) DEFAULT NULL,
  `Sub208` varchar(100) DEFAULT NULL,
  `Sub209` varchar(100) DEFAULT NULL,
  `Sub210` varchar(100) DEFAULT NULL,
  `Sub211` varchar(100) DEFAULT NULL,
  `Sub212` varchar(100) DEFAULT NULL,
  `Sub213` varchar(100) DEFAULT NULL,
  `Sub214` varchar(100) DEFAULT NULL,
  `Sub215` varchar(100) DEFAULT NULL,
  `Sub216` varchar(100) DEFAULT NULL,
  `Sub217` varchar(100) DEFAULT NULL,
  `Sub218` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `SubgraphOccurrence_ht101_eswa`
--

CREATE TABLE IF NOT EXISTS `SubgraphOccurrence_ht101_eswa` (
  `Id` int(100) unsigned NOT NULL,
  `grafo` varchar(100) DEFAULT NULL,
  `Sub1` varchar(100) DEFAULT NULL,
  `Sub2` varchar(100) DEFAULT NULL,
  `Sub3` varchar(100) DEFAULT NULL,
  `Sub4` varchar(100) DEFAULT NULL,
  `Sub5` varchar(100) DEFAULT NULL,
  `Sub6` varchar(100) DEFAULT NULL,
  `Sub7` varchar(100) DEFAULT NULL,
  `Sub8` varchar(100) DEFAULT NULL,
  `Sub9` varchar(100) DEFAULT NULL,
  `Sub10` varchar(100) DEFAULT NULL,
  `Sub11` varchar(100) DEFAULT NULL,
  `Sub12` varchar(100) DEFAULT NULL,
  `Sub13` varchar(100) DEFAULT NULL,
  `Sub14` varchar(100) DEFAULT NULL,
  `Sub15` varchar(100) DEFAULT NULL,
  `Sub16` varchar(100) DEFAULT NULL,
  `Sub17` varchar(100) DEFAULT NULL,
  `Sub18` varchar(100) DEFAULT NULL,
  `Sub19` varchar(100) DEFAULT NULL,
  `Sub20` varchar(100) DEFAULT NULL,
  `Sub21` varchar(100) DEFAULT NULL,
  `Sub22` varchar(100) DEFAULT NULL,
  `Sub23` varchar(100) DEFAULT NULL,
  `Sub24` varchar(100) DEFAULT NULL,
  `Sub25` varchar(100) DEFAULT NULL,
  `Sub26` varchar(100) DEFAULT NULL,
  `Sub27` varchar(100) DEFAULT NULL,
  `Sub28` varchar(100) DEFAULT NULL,
  `Sub29` varchar(100) DEFAULT NULL,
  `Sub30` varchar(100) DEFAULT NULL,
  `Sub31` varchar(100) DEFAULT NULL,
  `Sub32` varchar(100) DEFAULT NULL,
  `Sub33` varchar(100) DEFAULT NULL,
  `Sub34` varchar(100) DEFAULT NULL,
  `Sub35` varchar(100) DEFAULT NULL,
  `Sub36` varchar(100) DEFAULT NULL,
  `Sub37` varchar(100) DEFAULT NULL,
  `Sub38` varchar(100) DEFAULT NULL,
  `Sub39` varchar(100) DEFAULT NULL,
  `Sub40` varchar(100) DEFAULT NULL,
  `Sub41` varchar(100) DEFAULT NULL,
  `Sub42` varchar(100) DEFAULT NULL,
  `Sub43` varchar(100) DEFAULT NULL,
  `Sub44` varchar(100) DEFAULT NULL,
  `Sub45` varchar(100) DEFAULT NULL,
  `Sub46` varchar(100) DEFAULT NULL,
  `Sub47` varchar(100) DEFAULT NULL,
  `Sub48` varchar(100) DEFAULT NULL,
  `Sub49` varchar(100) DEFAULT NULL,
  `Sub50` varchar(100) DEFAULT NULL,
  `Sub51` varchar(100) DEFAULT NULL,
  `Sub52` varchar(100) DEFAULT NULL,
  `Sub53` varchar(100) DEFAULT NULL,
  `Sub54` varchar(100) DEFAULT NULL,
  `Sub55` varchar(100) DEFAULT NULL,
  `Sub56` varchar(100) DEFAULT NULL,
  `Sub57` varchar(100) DEFAULT NULL,
  `Sub58` varchar(100) DEFAULT NULL,
  `Sub59` varchar(100) DEFAULT NULL,
  `Sub60` varchar(100) DEFAULT NULL,
  `Sub61` varchar(100) DEFAULT NULL,
  `Sub62` varchar(100) DEFAULT NULL,
  `Sub63` varchar(100) DEFAULT NULL,
  `Sub64` varchar(100) DEFAULT NULL,
  `Sub65` varchar(100) DEFAULT NULL,
  `Sub66` varchar(100) DEFAULT NULL,
  `Sub67` varchar(100) DEFAULT NULL,
  `Sub68` varchar(100) DEFAULT NULL,
  `Sub69` varchar(100) DEFAULT NULL,
  `Sub70` varchar(100) DEFAULT NULL,
  `Sub71` varchar(100) DEFAULT NULL,
  `Sub72` varchar(100) DEFAULT NULL,
  `Sub73` varchar(100) DEFAULT NULL,
  `Sub74` varchar(100) DEFAULT NULL,
  `Sub75` varchar(100) DEFAULT NULL,
  `Sub76` varchar(100) DEFAULT NULL,
  `Sub77` varchar(100) DEFAULT NULL,
  `Sub78` varchar(100) DEFAULT NULL,
  `Sub79` varchar(100) DEFAULT NULL,
  `Sub80` varchar(100) DEFAULT NULL,
  `Sub81` varchar(100) DEFAULT NULL,
  `Sub82` varchar(100) DEFAULT NULL,
  `Sub83` varchar(100) DEFAULT NULL,
  `Sub84` varchar(100) DEFAULT NULL,
  `Sub85` varchar(100) DEFAULT NULL,
  `Sub86` varchar(100) DEFAULT NULL,
  `Sub87` varchar(100) DEFAULT NULL,
  `Sub88` varchar(100) DEFAULT NULL,
  `Sub89` varchar(100) DEFAULT NULL,
  `Sub90` varchar(100) DEFAULT NULL,
  `Sub91` varchar(100) DEFAULT NULL,
  `Sub92` varchar(100) DEFAULT NULL,
  `Sub93` varchar(100) DEFAULT NULL,
  `Sub94` varchar(100) DEFAULT NULL,
  `Sub95` varchar(100) DEFAULT NULL,
  `Sub96` varchar(100) DEFAULT NULL,
  `Sub97` varchar(100) DEFAULT NULL,
  `Sub98` varchar(100) DEFAULT NULL,
  `Sub99` varchar(100) DEFAULT NULL,
  `Sub100` varchar(100) DEFAULT NULL,
  `Sub101` varchar(100) DEFAULT NULL,
  `Sub102` varchar(100) DEFAULT NULL,
  `Sub103` varchar(100) DEFAULT NULL,
  `Sub104` varchar(100) DEFAULT NULL,
  `Sub105` varchar(100) DEFAULT NULL,
  `Sub106` varchar(100) DEFAULT NULL,
  `Sub107` varchar(100) DEFAULT NULL,
  `Sub108` varchar(100) DEFAULT NULL,
  `Sub109` varchar(100) DEFAULT NULL,
  `Sub110` varchar(100) DEFAULT NULL,
  `Sub111` varchar(100) DEFAULT NULL,
  `Sub112` varchar(100) DEFAULT NULL,
  `Sub113` varchar(100) DEFAULT NULL,
  `Sub114` varchar(100) DEFAULT NULL,
  `Sub115` varchar(100) DEFAULT NULL,
  `Sub116` varchar(100) DEFAULT NULL,
  `Sub117` varchar(100) DEFAULT NULL,
  `Sub118` varchar(100) DEFAULT NULL,
  `Sub119` varchar(100) DEFAULT NULL,
  `Sub120` varchar(100) DEFAULT NULL,
  `Sub121` varchar(100) DEFAULT NULL,
  `Sub122` varchar(100) DEFAULT NULL,
  `Sub123` varchar(100) DEFAULT NULL,
  `Sub124` varchar(100) DEFAULT NULL,
  `Sub125` varchar(100) DEFAULT NULL,
  `Sub126` varchar(100) DEFAULT NULL,
  `Sub127` varchar(100) DEFAULT NULL,
  `Sub128` varchar(100) DEFAULT NULL,
  `Sub129` varchar(100) DEFAULT NULL,
  `Sub130` varchar(100) DEFAULT NULL,
  `Sub131` varchar(100) DEFAULT NULL,
  `Sub132` varchar(100) DEFAULT NULL,
  `Sub133` varchar(100) DEFAULT NULL,
  `Sub134` varchar(100) DEFAULT NULL,
  `Sub135` varchar(100) DEFAULT NULL,
  `Sub136` varchar(100) DEFAULT NULL,
  `Sub137` varchar(100) DEFAULT NULL,
  `Sub138` varchar(100) DEFAULT NULL,
  `Sub139` varchar(100) DEFAULT NULL,
  `Sub140` varchar(100) DEFAULT NULL,
  `Sub141` varchar(100) DEFAULT NULL,
  `Sub142` varchar(100) DEFAULT NULL,
  `Sub143` varchar(100) DEFAULT NULL,
  `Sub144` varchar(100) DEFAULT NULL,
  `Sub145` varchar(100) DEFAULT NULL,
  `Sub146` varchar(100) DEFAULT NULL,
  `Sub147` varchar(100) DEFAULT NULL,
  `Sub148` varchar(100) DEFAULT NULL,
  `Sub149` varchar(100) DEFAULT NULL,
  `Sub150` varchar(100) DEFAULT NULL,
  `Sub151` varchar(100) DEFAULT NULL,
  `Sub152` varchar(100) DEFAULT NULL,
  `Sub153` varchar(100) DEFAULT NULL,
  `Sub154` varchar(100) DEFAULT NULL,
  `Sub155` varchar(100) DEFAULT NULL,
  `Sub156` varchar(100) DEFAULT NULL,
  `Sub157` varchar(100) DEFAULT NULL,
  `Sub158` varchar(100) DEFAULT NULL,
  `Sub159` varchar(100) DEFAULT NULL,
  `Sub160` varchar(100) DEFAULT NULL,
  `Sub161` varchar(100) DEFAULT NULL,
  `Sub162` varchar(100) DEFAULT NULL,
  `Sub163` varchar(100) DEFAULT NULL,
  `Sub164` varchar(100) DEFAULT NULL,
  `Sub165` varchar(100) DEFAULT NULL,
  `Sub166` varchar(100) DEFAULT NULL,
  `Sub167` varchar(100) DEFAULT NULL,
  `Sub168` varchar(100) DEFAULT NULL,
  `Sub169` varchar(100) DEFAULT NULL,
  `Sub170` varchar(100) DEFAULT NULL,
  `Sub171` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `traceid`
--

CREATE TABLE IF NOT EXISTS `traceid` (
  `numTrace` varchar(500) NOT NULL,
  `idTrace` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='traceidwabototal';

-- --------------------------------------------------------

--
-- Struttura della tabella `traceiddisconnected`
--

CREATE TABLE IF NOT EXISTS `traceiddisconnected` (
  `numTrace` int(11) NOT NULL,
  `id_wf` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `traceidwabo`
--

CREATE TABLE IF NOT EXISTS `traceidwabo` (
  `numTrace` varchar(500) NOT NULL,
  `idTrace` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='traceidwabototal';

-- --------------------------------------------------------

--
-- Struttura della tabella `tracesgraph`
--

CREATE TABLE IF NOT EXISTS `tracesgraph` (
  `idgraph` int(11) NOT NULL,
  `numtraces` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `tracesgraphbackup`
--

CREATE TABLE IF NOT EXISTS `tracesgraphbackup` (
  `idgraph` int(11) NOT NULL,
  `numtraces` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `transition`
--

CREATE TABLE IF NOT EXISTS `transition` (
  `id` varchar(100) NOT NULL,
  `name` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura per la vista `discriminantfeat`
--
DROP TABLE IF EXISTS `discriminantfeat`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `discriminantfeat` AS (select `subgraphoccurrence_ht101_eswa`.`Sub11` AS `Sub11`,`subgraphoccurrence_ht101_eswa`.`Sub67` AS `Sub67`,`subgraphoccurrence_ht101_eswa`.`Sub72` AS `Sub72`,`subgraphoccurrence_ht101_eswa`.`Sub75` AS `Sub75`,`subgraphoccurrence_ht101_eswa`.`Sub96` AS `Sub96`,`subgraphoccurrence_ht101_eswa`.`Sub101` AS `Sub101`,`subgraphoccurrence_ht101_eswa`.`Sub113` AS `Sub113`,`subgraphoccurrence_ht101_eswa`.`Sub125` AS `Sub125`,`subgraphoccurrence_ht101_eswa`.`Sub129` AS `Sub129`,`subgraphoccurrence_ht101_eswa`.`Sub163` AS `Sub163`,`subgraphoccurrence_ht101_eswa`.`Sub169` AS `Sub169` from `subgraphoccurrence_ht101_eswa`);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `anomalies`
--
ALTER TABLE `anomalies`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `anomedges`
--
ALTER TABLE `anomedges`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `causalreltau`
--
ALTER TABLE `causalreltau`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `covgraph`
--
ALTER TABLE `covgraph`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `edge`
--
ALTER TABLE `edge`
  ADD PRIMARY KEY (`id`),
  ADD KEY `i1` (`source`);

--
-- Indici per le tabelle `edges`
--
ALTER TABLE `edges`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id1` (`id1`),
  ADD KEY `id2` (`id2`),
  ADD KEY `label` (`label`);

--
-- Indici per le tabelle `edgesbpinorep`
--
ALTER TABLE `edgesbpinorep`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id1` (`id1`),
  ADD KEY `id2` (`id2`),
  ADD KEY `label` (`label`),
  ADD KEY `id_wf` (`id_wf`);

--
-- Indici per le tabelle `nodes`
--
ALTER TABLE `nodes`
  ADD PRIMARY KEY (`pos`);

--
-- Indici per le tabelle `nodesbpinorep`
--
ALTER TABLE `nodesbpinorep`
  ADD PRIMARY KEY (`pos`),
  ADD KEY `id_wf` (`id_wf`);

--
-- Indici per le tabelle `place`
--
ALTER TABLE `place`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `SubgraphOccurrence`
--
ALTER TABLE `SubgraphOccurrence`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `SubgraphOccurrence_controlla`
--
ALTER TABLE `SubgraphOccurrence_controlla`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `SubgraphOccurrence_hdcc_eswa`
--
ALTER TABLE `SubgraphOccurrence_hdcc_eswa`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `SubgraphOccurrence_hdm13_eswa`
--
ALTER TABLE `SubgraphOccurrence_hdm13_eswa`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `SubgraphOccurrence_hdm13_eswa_new`
--
ALTER TABLE `SubgraphOccurrence_hdm13_eswa_new`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `SubgraphOccurrence_hdm16_eswa`
--
ALTER TABLE `SubgraphOccurrence_hdm16_eswa`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `SubgraphOccurrence_ht101_eswa`
--
ALTER TABLE `SubgraphOccurrence_ht101_eswa`
  ADD PRIMARY KEY (`Id`);

--
-- Indici per le tabelle `traceid`
--
ALTER TABLE `traceid`
  ADD PRIMARY KEY (`numTrace`);

--
-- Indici per le tabelle `traceidwabo`
--
ALTER TABLE `traceidwabo`
  ADD PRIMARY KEY (`numTrace`);

--
-- Indici per le tabelle `transition`
--
ALTER TABLE `transition`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `anomalies`
--
ALTER TABLE `anomalies`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `anomedges`
--
ALTER TABLE `anomedges`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `causalreltau`
--
ALTER TABLE `causalreltau`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `covgraph`
--
ALTER TABLE `covgraph`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `edges`
--
ALTER TABLE `edges`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `edgesbpinorep`
--
ALTER TABLE `edgesbpinorep`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `nodes`
--
ALTER TABLE `nodes`
  MODIFY `pos` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `nodesbpinorep`
--
ALTER TABLE `nodesbpinorep`
  MODIFY `pos` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence`
--
ALTER TABLE `SubgraphOccurrence`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence_controlla`
--
ALTER TABLE `SubgraphOccurrence_controlla`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence_hdcc_eswa`
--
ALTER TABLE `SubgraphOccurrence_hdcc_eswa`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence_hdm13_eswa`
--
ALTER TABLE `SubgraphOccurrence_hdm13_eswa`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence_hdm13_eswa_new`
--
ALTER TABLE `SubgraphOccurrence_hdm13_eswa_new`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence_hdm16_eswa`
--
ALTER TABLE `SubgraphOccurrence_hdm16_eswa`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT per la tabella `SubgraphOccurrence_ht101_eswa`
--
ALTER TABLE `SubgraphOccurrence_ht101_eswa`
  MODIFY `Id` int(100) unsigned NOT NULL AUTO_INCREMENT;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
