package org.example;

import com.mysql.jdbc.MysqlIO;
import com.mysql.jdbc.ResultSetInternalMethods;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.util.concurrent.Callable;


public class Interceptor {
    public static ResultSetInternalMethods sqlQueryDirect(
            @This
            Object zuper,
            @SuperCall
            Callable<ResultSetInternalMethods> client,
            @AllArguments
            Object[] args) throws Exception {

        Statement statement = (Statement) args[0];
        if (statement instanceof PreparedStatement) {
            try {
                String sql = ((PreparedStatement) statement).asSql(); // yeh input hojayega ki sql query particular
                InetSocketAddress inetSocketAddress = (InetSocketAddress) ((MysqlIO) zuper).mysqlConnection.getRemoteSocketAddress();
                int ipv4 = ByteBuffer.wrap(inetSocketAddress.getAddress().getAddress()).getInt();
                int port = inetSocketAddress.getPort();
                System.out.println("Inside sql Interceptor");
                System.out.println("ipv4 is " + ipv4 + ", port is " + port);
                ResultSetInternalMethods response = client.call();
                return response;
            } catch (Exception e) {
                throw e;
            }
        } else {
            try {
                ResultSetInternalMethods response = client.call();
                return response;
            } catch (Exception e) {
                throw e;
            }
        }
    }
}

