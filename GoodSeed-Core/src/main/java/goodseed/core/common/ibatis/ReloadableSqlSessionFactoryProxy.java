package goodseed.core.common.ibatis;

import java.sql.Connection;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;

/**
 *
 * The class ReloadableSqlSessionFactoryProxy<br>
 * <br>
 * Sql이 리로드 된 후 SqlSessionFactory를 복사해주기 위하여<br> 
 * SqlSessionFactory를 구현한 Proxy 클래스<br>
 * <br>
 * <br>
 * Copyright (c) 2014 GoodSeed<br>
 * All rights reserved.<br>
 * <br>
 * This software is the proprietary information of GoodSeed<br>
 * <br>
 * @author jay
 * @version 3.0
 * @since  3. 11.
 *
 */
public class ReloadableSqlSessionFactoryProxy implements SqlSessionFactory{

	private SqlSessionFactory sqlSessionFactory;
	
	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public SqlSession openSession() {
		return this.sqlSessionFactory.openSession();
	}

	@Override
	public SqlSession openSession(boolean autoCommit) {
		return this.sqlSessionFactory.openSession(autoCommit);
	}

	@Override
	public SqlSession openSession(Connection connection) {
		return this.sqlSessionFactory.openSession(connection);
	}

	@Override
	public SqlSession openSession(TransactionIsolationLevel level) {
		return this.sqlSessionFactory.openSession(level);
	}

	@Override
	public SqlSession openSession(ExecutorType execType) {
		return this.sqlSessionFactory.openSession(execType);
	}

	@Override
	public SqlSession openSession(ExecutorType execType, boolean autoCommit) {
		return this.sqlSessionFactory.openSession(execType, autoCommit);
	}

	@Override
	public SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level) {
		return this.sqlSessionFactory.openSession(execType, level);
	}

	@Override
	public SqlSession openSession(ExecutorType execType, Connection connection) {
		return this.sqlSessionFactory.openSession(execType, connection);
	}

	@Override
	public Configuration getConfiguration() {
		return this.sqlSessionFactory.getConfiguration();
	}

}
