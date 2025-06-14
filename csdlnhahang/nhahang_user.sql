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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `ID_User` int unsigned NOT NULL AUTO_INCREMENT,
  `TenUser` varchar(50) NOT NULL,
  `SDT` varchar(50) NOT NULL,
  `Email` varchar(50) NOT NULL,
  `MatKhau` varchar(255) NOT NULL,
  `Role` enum('SILVER','','GOLD','DIAMOND') DEFAULT 'SILVER',
  PRIMARY KEY (`ID_User`),
  UNIQUE KEY `SDT` (`SDT`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (19,'Lưu Đức Hiếu','0976981804','hieu@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(20,'Lưu Thu An','0969316038','an@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(21,'Nguyễn Thị Thu Hiền','0976180420','hien@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(22,'Lưu Đức Huynh','0986572917','huynh@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(23,'Nguyễn Văn Chính','0914162689','chinh@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(24,'Phạm Anh Dũng','0978178764','dung@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(25,'Đỗ Hải Đăng','0905682529','dang@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(26,'Phan Thanh Dũng','0979809204','dungpt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(27,'Nguyễn Thị Đào','0982725726','daont@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(28,'Nguyễn Thị Tuyết Đan','0937303282','danntt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(29,'Phạm Thị Điệp','0974131489','dieppt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(30,'Nguyễn Thị Gấm','0977557349','gamnt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(31,'Hoàng Thu Hằng','0349616718','hanght@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(32,'Trương Thị Hằng','0372389777','hangtt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(33,'Bùi Thị Hương','0986958908','huongbt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(34,'Hồ Thị Mai Hương','0373433886','huonghtm@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(35,'Nguyễn Thị Hương','0988753023','huongnt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(36,'Giản Thị Hà','0972907579','hagt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(37,'Chu Thị Hà','0904054732','hact@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(38,'Nguyễn Thị Ngọc Hà','0358102387','hantn@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(39,'Lê Thị Duyên Hải','0366240750','hailtd@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(40,'Lưu Thị Hảo','0983043195','haolt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(41,'Lương Thị Thảo','0976388281','thaolt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(42,'Phùng Thị Hiếu','0976096113','hieupt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(43,'Nguyễn Thị Như Hoa','0912992158','hoantn@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(44,'Đỗ Thị Huệ','0979389891','huedt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(45,'Phạm Thị Huế','0979680065','huept@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(46,'Vương Thị Huế','0976764388','huevt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(47,'Nguyễn Thị Minh Huyền','0975808375','huyenntm@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(48,'Nguyễn Thị Liễu','0986109656','lieunt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(49,'Nguyễn Thị Hồng Nhung','0905516826','nhungnth@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(50,'Phùng Mạnh Ninh','0977022810','ninhpm@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(51,'Đỗ Thị Quyên','0979006323','quyendt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(52,'Trương Thị Thanh Tâm','0979879310','tamttt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(53,'Đặng Vĩnh Tân','0989352144','tandv@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(54,'Tạ Thị Thanh Tú','0904690835','tuttt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(55,'Nguyễn Thùy Trang','0979650651','trangnt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER'),(56,'Trương Thị Yến','0975236626','yentt@gmail.com','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92','SILVER');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-04 19:19:40
