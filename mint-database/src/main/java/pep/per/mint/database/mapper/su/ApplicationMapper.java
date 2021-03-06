/*
 * Copyright 2013 ~ 2014 Mocomsys(dhkim, Solution TF), Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * Please contact Mocomsys, Inc., NURITKUM SQUARE R&D TOWER, 11F DMC 1605,
 * Sangam-Dong, Mapo-Gu, Seoul, 121-795 Korea or visit mocomsys.com
 * if you need additional information or have any questions.
*/
package pep.per.mint.database.mapper.su;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import pep.per.mint.common.data.basic.Application;




public interface ApplicationMapper {

	public List<Application> getApplicationList(Map params) throws Exception;

	public int insertApplication(Application service) throws Exception;

	public int updateApplication(Application service) throws  Exception;

	public int deleteApplication(Map params) throws Exception;

	public List<Application> existApplication(Map params) throws Exception;

	public List<String> usedApplication(@Param("appId")String appId) throws Exception;

	public int insertApplicationMappingUserRole(Application service)throws Exception;

	public int deleteApplicationMappingUserRole(Map params)throws Exception;

}
