package persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.Campionato;
import model.Partita;
import model.Squadra;
import persistence.dao.PartitaDao;

public class PartitaDaoJDBC implements PartitaDao {

	private DataSource dataSource;

	public PartitaDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	@Override
	public void save(Partita partita) {
		Connection connection = this.dataSource.getConnection();
		try {
			
			Partita esistente = findByPrimaryKey(partita.getCodice());
			if(esistente != null)
				return;
			String insert = "insert into partita(codice,squadraCasa,squadraOspite,campionato,data,finita,goalCasa,goalOspite) values (?,?,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, partita.getCodice());
			statement.setString(2, partita.getSquadra_casa().getNome());
			statement.setString(3,partita.getSquadra_ospite().getNome());
			statement.setLong(4,partita.getCampionato().getCodice());
			statement.setDate(5, new java.sql.Date(partita.getData_ora().getTime()));
			statement.setBoolean(6, partita.isFinita());
			statement.setInt(7,partita.getGoal_casa());
			statement.setInt(8,partita.getGoal_ospite());
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
	public Partita findByPrimaryKey(Long codice) {
		Connection connection = this.dataSource.getConnection();
		Partita partita = null;
		try {
			PreparedStatement statement;
			String query = "select p.codice,p.squadraCasa,p.squadraOspite,p.data,p.finita,p.goalCasa,p.goalOspite,c.codice,c.nome from partita as p, campionato as c where p.codice=? and p.campionato=c.codice "; 
			statement = connection.prepareStatement(query);
			statement.setLong(1, codice);
			ResultSet result = statement.executeQuery();
			if (result.next()) 
				partita=new Partita(result.getLong(1), new Squadra(result.getString(2)), new Squadra(result.getString(3)), result.getInt(6), result.getInt(7), new Campionato(result.getLong(8),result.getString(9)),result.getDate(4),result.getBoolean(5));
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}	
		return partita;
	}

	@Override
	public List<Partita> findAll() {
		Connection connection = this.dataSource.getConnection();
		List<Partita> partite = new LinkedList<>();
		try {
			
			PreparedStatement statement;
			String query = "select p.codice,p.squadraCasa,p.squadraOspite,p.data,p.finita,p.goalCasa,p.goalOspite,c.codice,c.nome from partita as p,campionato as c where p.campionato=c.codice";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while (result.next()) {
				
				Partita partita=new Partita(result.getLong(1), new Squadra(result.getString(2)), new Squadra(result.getString(3)), result.getInt(6), result.getInt(7), new Campionato(result.getLong(8),result.getString(9)),result.getDate(4),result.getBoolean(5));
				partite.add(partita);
			}

		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return partite;
	}
	
}