package SVM.FeatureSelection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DataPrepare.DatabaseCom;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseCom dc = new DatabaseCom();
		try (Connection con = dc.Getconnection()) {
			String sqlTrain = "select Topic,Title,Body from train_set where not(Title='' && Body='')";
			try (PreparedStatement sql = con.prepareStatement(sqlTrain)) {
				ResultSet rs = sql.executeQuery();
				while (rs.next()) {
					rs.getString("Topic");
					rs.getString("Title");
					rs.getString("Body");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
