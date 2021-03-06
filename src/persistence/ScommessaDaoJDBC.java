package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Conto;
import model.EsitoPartita;
import model.MovimentoScommessa;
import model.Partita;
import model.SchemaDiScommessa;
import model.Scommessa;
import model.Squadra;
import model.TipoMovimento;
import persistence.dao.MovimentoScommessaDao;
import persistence.dao.ScommessaDao;

public class ScommessaDaoJDBC implements ScommessaDao {

	@Override
	public void save(Scommessa scommessa) {
		Connection connection = PostgresDAOFactory.dataSource.getConnection();
		try {
			String insert = "insert into scommessa(codice, data_emissione, conto_associato, importo_giocato,quota_totale,bonus,numero_esiti,vincita_potenziale) values (?,?,?,?,?,?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, scommessa.getCodice());
			statement.setDate(2, new java.sql.Date(scommessa.getData_emissione().getTime()));
			statement.setLong(3, scommessa.getConto_associato().getCodice());
			statement.setFloat(4, scommessa.getSchema_scommessa().getImporto_giocato());
			statement.setFloat(5, scommessa.getSchema_scommessa().getQuota_totale());
			statement.setFloat(6, scommessa.getSchema_scommessa().getBonus());
			statement.setFloat(7, scommessa.getSchema_scommessa().getNumero_esiti());
			statement.setFloat(8, scommessa.getSchema_scommessa().getVincita_potenziale());
			statement.executeUpdate();
			
			for(EsitoPartita esito_giocato : scommessa.getSchema_scommessa().getEsiti_giocati()) {
				insert="insert into scommessa_esitopartita(scommessa, esito, partita) values (?,?,?)";
				statement=connection.prepareStatement(insert);
				statement.setLong(1, scommessa.getCodice());
				statement.setString(2, esito_giocato.getEsito().getDescrizione());
				statement.setLong(3, esito_giocato.getPartita().getCodice());
				statement.executeUpdate();
			}
			MovimentoScommessa movimento = new MovimentoScommessa(scommessa.getSchema_scommessa().getImporto_giocato(), TipoMovimento.PRELIEVO,scommessa);
			MovimentoScommessaDao movimentoDao = PostgresDAOFactory.getDAOFactory(PostgresDAOFactory.POSTGRESQL).getMovimentoScommessaDAO();
			movimentoDao.save(movimento);
			
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
	public Scommessa findByPrimaryKey(Long codice) {
		Connection connection = PostgresDAOFactory.dataSource.getConnection();
		Scommessa scommessa = null;
		try {
			PreparedStatement statement;
			String query = "select s.dataemissione, s.contoassociato, s.importo_giocato,s.quota_totale,s.bonus,s.numero_esiti,"
					+ "s.vincita_potenziale from scommessa as s where s.codice=? "; 
			statement = connection.prepareStatement(query);
			statement.setLong(1, codice);
			ResultSet result = statement.executeQuery();
			if (result.next()){
				ArrayList<EsitoPartita> esiti_giocati = new ArrayList<EsitoPartita>();
				//controllare se va bene discutere ( aggiungere il join con la partita
				query = "select e.esito,e.quota,p.codice,p.squadraCasa,p.squadraOspite,p.data from scommessa_esitopartita as se, esitopartita as e, partita as p where se.partita=e.partita and se.esito = e.esito and e.partita=p.codice and se.scommessa=?";
				statement=connection.prepareStatement(query);
				statement.setLong(1, codice);
				ResultSet result2 = statement.executeQuery();
				while (result2.next()) {
					EsitoPartita corrente = new EsitoPartita(true,new model.Esito(result.getString(1)), result.getFloat(2), new Partita(result.getLong(3),new Squadra(result.getString(4)),new Squadra(result.getString(5)),-1,-1,null,result.getDate(6).getTime(),false));
					esiti_giocati.add(corrente);
				}
				SchemaDiScommessa schema_scommessa  = new SchemaDiScommessa(result.getFloat(3), result.getFloat(4), result.getFloat(5), result.getInt(6), result.getFloat(7), esiti_giocati);
				scommessa=new Scommessa(codice, result.getDate(1), new Conto(result.getLong(2), 0.0f , null, null),schema_scommessa);
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
		return scommessa;
	}

	@Override
	public List<Scommessa> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Scommessa scommessa) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Scommessa scommessa) {
		// TODO Auto-generated method stub

	}

}
