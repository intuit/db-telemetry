package com.intuit.dbtelemetry.sql.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SqlHelperUtilsTests {
    public static final String insertQuery = "/* insert com.intuit.sbd.payroll.psp.domain.FinancialTransactionState */ insert into PSP_FINANCIAL_TRANS_STATE (VERSION, CREATOR_ID, CREATED_DATE, MODIFIER_ID, MODIFIED_DATE, REALM_ID, TRANSACTION_STATE_EFF_DATE, INSERT_USER_ID, GEMS_UPLOAD_BATCH_FK, COMPANY_FK, FINANCIAL_TRANSACTION_FK, TRANSACTION_STATE_FK, TRANSACTION_RESPONSE_FK, TRANSACTION_TYPE_FK, FINANCIAL_TRANS_STATE_SEQ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    final String noChange = "/* criteria query */ select this_.SYSTEM_PARAMETER_SEQ as SYSTEM1_248_0_, this_.VERSION as VERSION248_0_, this_.CREATOR_ID as CREATOR3_248_0_, this_.CREATED_DATE as CREATED4_248_0_, this_.MODIFIER_ID as MODIFIER5_248_0_, this_.MODIFIED_DATE as MODIFIED6_248_0_, this_.REALM_ID as REALM7_248_0_, this_.SYSTEM_PARAMETER_CD as SYSTEM8_248_0_, this_.SYSTEM_PARAMETER_DESCRIPTION as SYSTEM9_248_0_, this_.SYSTEM_PARAMETER_ORG as SYSTEM10_248_0_, this_.SYSTEM_PARAMETER_VALUE as SYSTEM11_248_0_, this_.IS_SECURED as IS12_248_0_ from PSP_SYSTEM_PARAMETER this_ where this_.SYSTEM_PARAMETER_CD=?";
    final String changeParameter = "/* named native SQL query findCurrentProcessCacheId */ select SYSTEM_PARAMETER_VALUE as processCacheToken\n" +
            "            from PSP_SYSTEM_PARAMETER\n" +
            "            where SYSTEM_PARAMETER_CD = 'PROCESS_CACHE_REFRESH_TOKEN'";
    final String changeParameterResult = "/* named native SQL query findCurrentProcessCacheId */ select SYSTEM_PARAMETER_VALUE as processCacheToken\n" +
            "            from PSP_SYSTEM_PARAMETER\n" +
            "            where SYSTEM_PARAMETER_CD = ?";
    final String deleteQuery = "DELETE FROM PSP_VMP_EMPLOYEE_INFO";
    final String deleteQueryResult = "DELETE FROM PSP_VMP_EMPLOYEE_INFO";
    final String updateQuery = "UPDATE PSP_PMT_TEMPLATE_BANKACCOUNT set status_cd = 'Active' where status_cd != 'Active' and creator_id != 'System'";
    final String updateQueryResult = "UPDATE PSP_PMT_TEMPLATE_BANKACCOUNT set status_cd = ? where status_cd != ? and creator_id != ?";
    final String insertQueryResult = "/* insert com.intuit.sbd.payroll.psp.domain.FinancialTransactionState */ insert into PSP_FINANCIAL_TRANS_STATE (VERSION, CREATOR_ID, CREATED_DATE, MODIFIER_ID, MODIFIED_DATE, REALM_ID, TRANSACTION_STATE_EFF_DATE, INSERT_USER_ID, GEMS_UPLOAD_BATCH_FK, COMPANY_FK, FINANCIAL_TRANSACTION_FK, TRANSACTION_STATE_FK, TRANSACTION_RESPONSE_FK, TRANSACTION_TYPE_FK, FINANCIAL_TRANS_STATE_SEQ) values (?)";

    final String procedure = "{call PRC_OFFLOAD_INSERT_FTS(?, ?, ?, ?)}";
    final String procedureResult = "{call PRC_OFFLOAD_INSERT_FTS(?)}";

    @Test
    public void sanitizeSQLNoChange() {
        Assert.assertEquals(noChange, SqlHelperUtils.sanitizeSQL(noChange));
    }

    @Test
    public void sanitizeSQLParameterChange() {
        Assert.assertEquals(changeParameterResult, SqlHelperUtils.sanitizeSQL(changeParameter));
    }

    @Test
    public void sanitizeSQLDeleteQuery() {
        Assert.assertEquals(deleteQueryResult, SqlHelperUtils.sanitizeSQL(deleteQuery));
    }

    @Test
    public void sanitizeSQLUpdateQuery() {
        Assert.assertEquals(updateQueryResult, SqlHelperUtils.sanitizeSQL(updateQuery));
    }

    @Test
    public void sanitizeSQLInsertQuery() {
        Assert.assertEquals(insertQueryResult, SqlHelperUtils.sanitizeSQL(insertQuery));
    }

    @Test
    public void sanitizeSQLProcedure() {
        Assert.assertEquals(procedureResult, SqlHelperUtils.sanitizeSQL(procedure));
    }


}
