package persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import model.Conto;
import persistence.dao.ContoDao;

public class ContoDaoJDBC implements ContoDao {

	public ContoDaoJDBC() {
	}

	@Override
	public void save(Conto conto) {
		Connection connection = PostgresDAOFactory.dataSource.getConnection();
		try {
			String insert = "insert into conto(codice, saldo, data_apertura, codice_carta) values (?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1,conto.getCodice());
			statement.setFloat(2,conto.getSaldo());
			statement.setDate(3, null);
			statement.setString(4, conto.getCarta().getCodiceCarta());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public Conto findByPrimaryKey(String matricola) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Conto> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Conto conto) {
		
		Connection connection = PostgresDAOFactory.dataSource.getConnection();
		try {
			String update = "update conto SET saldo = ? where codice=? ";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setFloat(1, conto.getSaldo());
			statement.setLong(2, conto.getCodice());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public void delete(Conto conto) {
		// TODO Auto-generated method stub
		
	}

	
}
