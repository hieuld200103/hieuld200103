-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: nhahang
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `monan`
--

DROP TABLE IF EXISTS `monan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monan` (
  `ID_MonAn` int unsigned NOT NULL AUTO_INCREMENT,
  `TenMon` varchar(50) NOT NULL,
  `Gia` int NOT NULL,
  `Mota` longtext NOT NULL,
  `LoaiMonAn` enum('KHAI_VI','MON_CHINH','TRANG_MIENG','DO_UONG') NOT NULL,
  `DanhMuc` enum('SOUP','SALAD','DO_CHIEN','MON_BO','MON_GA','HAI_SAN','BANH_NGOT','KEM','HOA_QUA','RUOU','NUOC_NGOT') NOT NULL,
  PRIMARY KEY (`ID_MonAn`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monan`
--

LOCK TABLES `monan` WRITE;
/*!40000 ALTER TABLE `monan` DISABLE KEYS */;
INSERT INTO `monan` VALUES (1,'Salad sò điệp',199000,'Vị ngọt giòn dai của sò điệp kết hợp cùng vị béo bùi của quả bơ','KHAI_VI','SALAD'),(2,'Mực chiên xù',139000,'Mực chiên xù với lớp vỏ ngoài bằng bột vàng vươm, giòn tan','KHAI_VI','DO_CHIEN'),(3,'Greek salad',159000,'Greek salad gồm nhiều các loại rau củ tươi ngon như cà chua, dưa leo, hành tây,..','KHAI_VI','SALAD'),(4,'Súp kem nấm',129000,'Súp nóng hổi, đậm đà với mùi thơm dịu từ xạ hương và ngò','KHAI_VI','SOUP'),(5,'Súp kem bí đỏ',129000,'Bí đỏ được hầm nhừ, xay nhuyễn rồi nấu cùng với lớp kem tươi sánh mịn','KHAI_VI','SOUP'),(6,'Salad hoa quả sữa chua',99000,'Sữa chua hoa quả được làm từ các loại trái cây tươi ngon trộn đều với sữa chua thanh mát','KHAI_VI','SALAD'),(7,'Súp kem măng tây',159000,'Màu xanh bắt mắt và sánh mịn của súp kem măng tây nóng hổi, đậm vị','KHAI_VI','SOUP'),(8,'Tôm bọc hạnh nhân',159000,'Phần thịt tôm săn chắc, lớp hạnh nhân béo bùi, ăn kèm cùng một chút rau củ','KHAI_VI','DO_CHIEN'),(9,'Salad sò điệp sốt wasabi',229000,'Wasabi cay nồng đặc trưng quyện với các loại rau củ và hải sản','KHAI_VI','SALAD'),(10,'Salad cá hồi xông khói',229000,'Miếng cá mềm thơm hòa quyện với nước sốt sữa chua thanh thanh ','KHAI_VI','SALAD'),(11,'Gỏi mực ThaiLan',159000,'Mực tươi trụng chín, trộn cùng hành tây, rau thơm, ớt và nước sốt chua cay kiểu Thái','KHAI_VI','SALAD'),(12,'Súp cua',159000,'Hương vị đậm đà của cua cùng nhiều loại hương liệu','KHAI_VI','SOUP'),(13,'Khoai tây chiên',99000,'Khoai đc chiên ngập dầu vàng rụm','KHAI_VI','DO_CHIEN'),(14,'Ngô chiên',99000,'Ngô chiên với bột giòn rụm','KHAI_VI','DO_CHIEN'),(15,'Mỳ Ý hải sản',569000,'Sợi mì mềm dai, từng miếng tôm mực đậm đà thấm đậm nước sốt của rau củ.','MON_CHINH','HAI_SAN'),(16,'Bò bít tết sốt tiêu đen kiểu Pháp',599000,'Phần thịt bò được áp chảo vừa chín tới, giữ được độ ngọt mềm của thịt, kết hợp với nước sốt kem tiêu cay nồng','MON_CHINH','MON_BO'),(17,'Tôm hùm nướng phô mai',599000,'Phần tôm ngọt thịt, săn chắc kết hợp với lớp phô mai beo béo','MON_CHINH','HAI_SAN'),(18,'Pizza sốt pesto gà',599000,'Phần đế bánh được nướng giòn, phần nhân là sự hòa quyện của ức gà, hành tím, phô mai cùng với sốt pesto mặn mặn beo béo','MON_CHINH','MON_GA'),(19,'Bò bít tết sốt kem',599000,'Hương vị thịt bò thượng hạng chín mềm, ngọt thịt kết hợp cùng sốt kem mặn mặn','MON_CHINH','MON_BO'),(20,'Ravioli nhân trứng chảy',599000,'Ravioli nhân phô mai trứng chảy có lớp vỏ vàng tươi, thơm dịu mùi ngò và húng tây','MON_CHINH','MON_GA'),(21,'Gà nướng kiểu Anh',599000,'Gà được nướng vàng với các nguyên liệu đặc trưng lạ miệng như ngò tây, lá xô thơm, lá thyme','MON_CHINH','MON_GA'),(22,'Ức gà sốt cam',599000,'Phần ức gà được áp chảo vàng đều, lớp da mỏng hơi béo và phần thịt gà mềm mọng ăn cùng với nước sốt cam chua ngọt dịu nhẹ','MON_CHINH','MON_GA'),(23,'Bò hầm rượu vang',629000,'Bò hầm rượu vang thơm nức mũi với phần bắp bò được hầm mềm nhưng vẫn giữ lại độ dai giòn của thịt','MON_CHINH','MON_BO'),(24,'Sườn nướng cam',599000,'Sườn non được nướng hơi xém nhưng vẫn giữ được độ ngọt và ẩm của thịt','MON_CHINH','MON_BO'),(25,'Mỳ Ý cua',599000,'Sợi mì vàng óng, dai mềm ăn kèm với thịt cua tươi ngon và phần sốt gạch cua thơm phức, đậm vị ','MON_CHINH','HAI_SAN'),(26,'Bò sốt vang Marzano',629000,'Thịt bò hầm mềm với rượu vang đỏ và cà chua Marzano, tạo nên nước sốt đậm vị, thơm nồng','MON_CHINH','MON_BO'),(27,'Panna cotta chanh dây',129000,'Phần pudding mềm béo từ sữa và mật ong, kết hợp với vị chua nhè nhẹ của chanh dây','TRANG_MIENG','KEM'),(28,'Bánh socola lava',159000,'Cốt bánh mềm tan quyện với phần nhân socola sánh mịn','TRANG_MIENG','BANH_NGOT'),(29,'Bánh brownie',129000,'Bánh có màu nâu đỏ bắt mắt cùng với độ mềm ẩm và ngọt đắng đặc trưng của socola','TRANG_MIENG','BANH_NGOT'),(30,'Bông lan flan',129000,'Lớp cốt bánh xốp bùi, thơm dịu kết hợp với lớp flan mềm mướt, béo ngậy','TRANG_MIENG','BANH_NGOT'),(31,'Mousse Xoài',129000,'Mousse xoài đẹp mắt với lớp thạch sánh mịn, thanh mát và phần cốt bánh mềm mướt, thơm dịu','TRANG_MIENG','HOA_QUA'),(32,'Bánh cheesecake cà phê',129000,'Bánh cheesecake cà phê kết hợp hoàn hảo vị phô mai béo ngậy và vị cà phê đăng đắn','TRANG_MIENG','BANH_NGOT'),(33,'Cabernet Sauvignon Chile',199000,'Vang đỏ đậm, hương vị quả mọng, gỗ sồi và chút tiêu đen. Phù hợp món bò.','DO_UONG','RUOU'),(34,'Merlot Pháp',259000,'Vang đỏ dịu nhẹ, vị trái cây chín, mềm mại, dễ uống.','DO_UONG','RUOU'),(35,'Chardonnay California',279000,'Vang trắng vị thanh mát, hậu béo bơ nhẹ, hợp với hải sản, salad.','DO_UONG','RUOU'),(36,'Shiraz Úc',399000,'Vị cay nhẹ, thơm mùi việt quất và mận, hậu vị kéo dài.','DO_UONG','RUOU'),(37,'Prosecco Ý',299000,'Vang trắng sủi bọt, vị ngọt nhẹ, tươi mát, dùng khai vị.','DO_UONG','RUOU'),(38,'Coca-Cola',25000,'Nước ngọt có ga vị cola truyền thống, dùng với đá lạnh.','DO_UONG','NUOC_NGOT'),(39,'Pepsi',25000,'Vị ngọt dịu, có ga, mát lạnh sảng khoái.','DO_UONG','NUOC_NGOT'),(40,'Sprite',25000,'Nước chanh có ga, vị thanh mát, không màu.','DO_UONG','NUOC_NGOT'),(41,'Fanta Cam',25000,'Vị cam ngọt thơm, màu sắc bắt mắt, dành cho trẻ nhỏ.','DO_UONG','NUOC_NGOT'),(42,'Aquafina',15000,'Nước lọc tinh khiết, chai nhựa 500ml.','DO_UONG','NUOC_NGOT');
/*!40000 ALTER TABLE `monan` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-04 17:11:28
