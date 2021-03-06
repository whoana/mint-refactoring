CREATE TABLE "QA_DETAILLOG"
(
    "MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"DETAILLOG_ID" NUMBER(7,0) NOT NULL ENABLE,
	"MSG_DATETIME" VARCHAR2(20 BYTE) NOT NULL ENABLE,
	"PR_PROCESS_MODE" VARCHAR2(16 BYTE),
	"PR_PROCESS_TYPE" VARCHAR2(16 BYTE),
	"PR_DT" DATE,
	"MSG_STATUS" CHAR(2 BYTE),
	"PR_HOP_CNT" NUMBER(8,0),
	"MQMD_QMGR" VARCHAR2(48 BYTE),
	"REPLY_QMGR" VARCHAR2(48 BYTE),
	"REPLY_QUEUE" VARCHAR2(48 BYTE),
	"RECORD_CNT" NUMBER(8,0),
	"RECORD_SIZE" NUMBER(16,0),
	"DATA_SIZE" NUMBER(16,0),
	"COMPRESS_YN" CHAR(1 BYTE),
	"COMPRESS_METHOD" VARCHAR2(20 BYTE),
	"COMPRESS_MODE" VARCHAR2(8 BYTE),
	"COMPRESS_SIZE" NUMBER(16,0),
	"CONV_YN" CHAR(1 BYTE),
	"CONV_MODE" VARCHAR2(8 BYTE),
	"CONV_SIZE" NUMBER(16,0),
	"LINK_CODE" VARCHAR2(5 BYTE),
	"ADAPTER_CODE" VARCHAR2(10 BYTE),
	"RECORD_S_CNT" NUMBER,
	"RECORD_E_CNT" NUMBER,
	"PROCESS_TIME" VARCHAR2(20 BYTE),
	 CONSTRAINT "PK_QA_DETAILLOG" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID", "DETAILLOG_ID")
) ;


  CREATE TABLE "QA_DETAILLOG_CUSTOM"
   (	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"DETAILLOG_ID" NUMBER NOT NULL ENABLE,
	"MSG_DATETIME" VARCHAR2(20 BYTE) NOT NULL ENABLE,
	"DETAIL01" VARCHAR2(1024 BYTE),
	"DETAIL02" VARCHAR2(1024 BYTE),
	"DETAIL03" VARCHAR2(1024 BYTE),
	"DETAIL04" VARCHAR2(1024 BYTE),
	"DETAIL05" VARCHAR2(1024 BYTE),
	"DETAIL06" VARCHAR2(1024 BYTE),
	"DETAIL07" VARCHAR2(1024 BYTE),
	"DETAIL08" VARCHAR2(1024 BYTE),
	"DETAIL09" VARCHAR2(1024 BYTE),
	"DETAIL10" VARCHAR2(1024 BYTE),
	 CONSTRAINT "PK_QA_DETAILLOG_CUSTOM" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID", "DETAILLOG_ID")
   );


  CREATE TABLE "QA_DETAILLOG_DATA"
   (	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"DETAILLOG_ID" NUMBER(7,0) NOT NULL ENABLE,
	"MSG_DATETIME" VARCHAR2(20 BYTE) NOT NULL ENABLE,
	"DATA" BLOB,
	 CONSTRAINT "PK_QA_DETAILLOG_DATA" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID", "DETAILLOG_ID")
   ) ;



  CREATE TABLE "QA_DETAILLOG_ERR"
   (
   	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"DETAILLOG_ID" NUMBER(7,0) NOT NULL ENABLE,
	"MSG_DATETIME" VARCHAR2(20 BYTE) NOT NULL ENABLE,
	"ERROR_TYPE" VARCHAR2(16 BYTE),
	"ERROR_CODE" VARCHAR2(10 BYTE),
	"RESP_TYPE" VARCHAR2(16 BYTE),
	"RESP_CODE" VARCHAR2(10 BYTE),
	"REASON_CODE" NUMBER(4,0),
	"ERROR_MSG" VARCHAR2(2048 BYTE),
	"ERRORQ_MSG_ID" VARCHAR2(48 BYTE),
	"ERRORQ_NM" VARCHAR2(48 BYTE),
	"TARGETQ_NM" VARCHAR2(48 BYTE),
  CONSTRAINT "PK_QA_DETAILLOG_ERR" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID", "DETAILLOG_ID")
   ) ;

  CREATE TABLE "QA_DETAILLOG_FILE"
   (	"MASTERLOG_ID" NUMBER,
	"DETAILLOG_ID" NUMBER(7,0),
	"MSG_DATETIME" VARCHAR2(20 BYTE),
	"DIRECTORY_NM" VARCHAR2(256 BYTE),
	"FILE_NM" VARCHAR2(256 BYTE),
	"FILE_SIZE" NUMBER(16,0),
	"EXT_PROGRAM" VARCHAR2(20 BYTE),
	"LEGAL" VARCHAR2(32 BYTE),
	 CONSTRAINT "PK_QA_DETAILLOG_FILE" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID", "DETAILLOG_ID")
   ) ;





  CREATE TABLE "QA_DETAILLOG_ID_MAP"
   (	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"DETAILLOG_ID" NUMBER(7,0) NOT NULL ENABLE,
	"PR_PROCESS_ID" VARCHAR2(50 BYTE) NOT NULL ENABLE,
	"PR_HOST_ID" VARCHAR2(36 BYTE),
	 CONSTRAINT "PK_QA_DETAILLOG_ID_MAP" PRIMARY KEY ("MASTERLOG_ID", "DETAILLOG_ID")
   ) ;


  CREATE INDEX "IDX1_QA_DETAILLOG_ID_MAP" ON "IIP"."QA_DETAILLOG_ID_MAP" ("MASTERLOG_ID", "DETAILLOG_ID", "PR_HOST_ID", "PR_PROCESS_ID") ;



  CREATE TABLE "QA_MASTERLOG"
   (	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"MSG_DATETIME" VARCHAR2(20 BYTE) NOT NULL ENABLE,
	"MSG_STATUS" CHAR(2 BYTE),
	"HUB_CNT" NUMBER(8,0),
	"SPOKE_CNT" NUMBER(8,0),
	"RECV_HUB_CNT" NUMBER(8,0),
	"RECV_SPOKE_CNT" NUMBER(8,0),
	"ERROR_TYPE" VARCHAR2(16 BYTE),
	"ERROR_CNT" NUMBER(16,0),
	"REASON_CODE" NUMBER(4,0),
	"MSG_TTL" DATE DEFAULT SYSDATE,
	"MSG_ALERT" CHAR(1 BYTE) DEFAULT 'N',
	"RECORD_CNT" NUMBER(8,0),
	"ADAPTER_CODE" VARCHAR2(10 BYTE),
	"RECORD_SIZE" NUMBER(16,0),
	"DATA_SIZE" NUMBER(16,0),
	"COMPRESS_YN" CHAR(1 BYTE),
	"COMPRESS_SIZE" NUMBER(16,0),
	"SOURCE_DIR_NM" VARCHAR2(128 BYTE),
	"SOURCE_FILE_NM" VARCHAR2(64 BYTE),
	"TARGET_DIR_NM" VARCHAR2(128 BYTE),
	"TARGET_FILE_NM" VARCHAR2(64 BYTE),
	"RECORD_S_CNT" NUMBER,
	"RECORD_E_CNT" NUMBER,
	"P_TIME" VARCHAR2(48 BYTE),
	"GLOBAL_ID" VARCHAR2(100 BYTE),
	"RECV_HOST_ID" VARCHAR2(4000 BYTE),
	 CONSTRAINT "PK_QA_MASTERLOG" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID")
   ) ;


  CREATE UNIQUE INDEX "IX_QA_MASTERLOG_MSDT_01" ON "QA_MASTERLOG" ("MSG_DATETIME", "MASTERLOG_ID", "MSG_STATUS")  ;


  CREATE TABLE "QA_MASTERLOG_CUSTOM"
   (	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"MSG_DATETIME" VARCHAR2(20 BYTE) NOT NULL ENABLE,
	"MASTER01" VARCHAR2(1024 BYTE),
	"MASTER02" VARCHAR2(1024 BYTE),
	"MASTER03" VARCHAR2(1024 BYTE),
	"MASTER04" VARCHAR2(1024 BYTE),
	"MASTER05" VARCHAR2(1024 BYTE),
	"MASTER06" VARCHAR2(1024 BYTE),
	"MASTER07" VARCHAR2(1024 BYTE),
	"MASTER08" VARCHAR2(1024 BYTE),
	"MASTER09" VARCHAR2(1024 BYTE),
	"MASTER010" VARCHAR2(1024 BYTE),
	 CONSTRAINT "PK_QA_MASTERLOG_CUSTOM" PRIMARY KEY ("MSG_DATETIME", "MASTERLOG_ID")
   ) ;


  CREATE TABLE "QA_MASTERLOG_ID_MAP"
   (	"MASTERLOG_ID" NUMBER NOT NULL ENABLE,
	"HOST_ID" VARCHAR2(36 BYTE),
	"GROUP_ID" VARCHAR2(48 BYTE),
	"INTF_ID" VARCHAR2(48 BYTE),
	"ADAPTER_CODE" VARCHAR2(10 BYTE),
	 CONSTRAINT "PK_QA_MASTERLOG_ID_MAP" PRIMARY KEY ("MASTERLOG_ID")
   )  ;


  CREATE INDEX "IDX1_QA_MASTERLOG_ID_MAP" ON "IIP"."QA_MASTERLOG_ID_MAP" ("MASTERLOG_ID", "GROUP_ID", "INTF_ID", "HOST_ID") ;


  CREATE TABLE "QA_TBS"
   (	"ID" VARCHAR2(20 BYTE),
	"ROOT_YN" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE,
	"GRP_YN" VARCHAR2(1 BYTE) DEFAULT 'N' NOT NULL ENABLE,
	 PRIMARY KEY ("ID")
   ) ;

