/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.test.hasor.db._06_transaction.simple.MANDATORY;
import static net.test.hasor.test.utils.HasorUnit.newID;
import java.sql.Connection;
import java.sql.SQLException;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.interceptor.simple.Transactional;
import net.test.hasor.db._06_transaction.simple.AbstractSimpleJDBCTest;
import net.test.hasor.db._07_datasource.warp.OneDataSourceWarp;
import net.test.hasor.test.junit.ContextConfiguration;
import net.test.hasor.test.runner.HasorUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * PROPAGATION_MANDATORY：要求环境中存在事务
 *   -条件：环境中没有事务，事务管理器会引发异常。
 * @version : 2013-12-10
 * @author 赵永春(zyc@hasor.net)
 */
@RunWith(HasorUnitRunner.class)
@ContextConfiguration(value = "net/test/simple/db/jdbc-config.xml", loadModules = OneDataSourceWarp.class)
public class NoTarn_MANDATORY_Test extends AbstractSimpleJDBCTest {
    @Test
    public void noTarn_MANDATORY_Test() throws Exception {
        System.out.println("--->>noTarn_MANDATORY_Test<<--");
        Thread.sleep(1000);
        /* 执行步骤：
         *   T1   ，新建‘默罕默德’用户           (打印：默罕默德).
         *      T2，开启事务                                (打印：No existing transaction found for transaction marked with propagation 'mandatory').
         *   T1   ，新建‘赵飞燕’用户               (打印：默罕默德、赵飞燕).
         */
        Connection conn = DataSourceUtils.getConnection(getDataSource());//申请连接
        {
            /*T1*/
            String insertUser = "insert into TB_User values(?,'默罕默德','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘默罕默德’...");
            new JdbcTemplate(conn).update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        {
            /*T2*/
            try {
                System.out.println("begin T2!");
                this.executeTransactional();
                System.out.println("commit T2!");
                throw new Exception("如果运行到这里就真的出错了。");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            Thread.sleep(1000);
        }
        {
            /*T1*/
            String insertUser = "insert into TB_User values(?,'赵飞燕','muhammad','123','muhammad@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘赵飞燕’...");
            new JdbcTemplate(conn).update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
        DataSourceUtils.releaseConnection(conn, getDataSource());//释放连接
    }
    //
    //
    @Transactional(propagation = Propagation.MANDATORY)
    public void executeTransactional() throws Exception {
        {
            String insertUser = "insert into TB_User values(?,'安妮.贝隆','belon','123','belon@hasor.net','2011-06-08 20:08:08');";
            System.out.println("insert new User ‘安妮.贝隆’...");
            this.getJdbcTemplate().update(insertUser, newID());//执行插入语句
            Thread.sleep(1000);
        }
    }
}