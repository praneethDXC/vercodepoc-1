package com.veracode.verademo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Random;

import javax.servlet.ServletContext;

import com.veracode.verademo.utils.Constants;
import com.veracode.verademo.utils.User;
import com.veracode.verademo.utils.Utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Scope("request")
public class ResetController {
	private static final Logger logger = LogManager.getLogger("VeraDemo:ResetController");

	@Autowired
	ServletContext context;

	private static User[] users = new User[] {
			User.create(System.getenv("USER_ADMIN"), System.getenv("PASS_ADMIN"), "Thats Mr Administrator to you."),
			User.create(System.getenv("USER_JOHN"), System.getenv("PASS_JOHN"), "John Smith"),
			User.create(System.getenv("USER_PAUL"), System.getenv("PASS_PAUL"), "Paul Farrington"),
			User.create(System.getenv("USER_CHRISC"), System.getenv("PASS_CHRISC"), "Chris Campbell"),
			User.create(System.getenv("USER_LAURIE"), System.getenv("PASS_LAURIE"), "Laurie Mercer"),
			User.create(System.getenv("USER_NABIL"), System.getenv("PASS_NABIL"), "Nabil Bousselham"),
			User.create(System.getenv("USER_JULIAN"), System.getenv("PASS_JULIAN"), "Julian Totzek-Hallhuber"),
			User.create(System.getenv("USER_JOASH"), System.getenv("PASS_JOASH"), "Joash Herbrink"),
			User.create(System.getenv("USER_ANDRZEJ"), System.getenv("PASS_ANDRZEJ"), "Andrzej Szaryk"),
			User.create(System.getenv("USER_APRIL"), System.getenv("PASS_APRIL"), "April Sauer"),
			User.create(System.getenv("USER_ARMANDO"), System.getenv("PASS_ARMANDO"), "Armando Bioc"),
			User.create(System.getenv("USER_BEN"), System.getenv("PASS_BEN"), "Ben Stoll"),
			User.create(System.getenv("USER_BRIAN"), System.getenv("PASS_BRIAN"), "Brian Pitta"),
			User.create(System.getenv("USER_CAITLIN"), System.getenv("PASS_CAITLIN"), "Caitlin Johanson"),
			User.create(System.getenv("USER_CHRISTRAUT"), System.getenv("PASS_CHRISTRAUT"), "Chris Trautwein"),
			User.create(System.getenv("USER_CHRISTYSON"), System.getenv("PASS_CHRISTYSON"), "Chris Tyson"),
			User.create(System.getenv("USER_CLINT"), System.getenv("PASS_CLINT"), "Clint Pollock"),
			User.create(System.getenv("USER_CODY"), System.getenv("PASS_CODY"), "Cody Bertram"),
			User.create(System.getenv("USER_DEREK"), System.getenv("PASS_DEREK"), "Derek Chowaniec"),
			User.create(System.getenv("USER_GLENN"), System.getenv("PASS_GLENN"), "Glenn Whittemore"),
			User.create(System.getenv("USER_GRANT"), System.getenv("PASS_GRANT"), "Grant Robinson"),
			User.create(System.getenv("USER_GREGORY"), System.getenv("PASS_GREGORY"), "Gregory Wolford"),
			User.create(System.getenv("USER_JACOB"), System.getenv("PASS_JACOB"), "Jacob Martel"),
			User.create(System.getenv("USER_JEREMY"), System.getenv("PASS_JEREMY"), "Jeremy Anderson"),
			User.create(System.getenv("USER_JOHNNY"), System.getenv("PASS_JOHNNY"), "Johnny Wong"),
			User.create(System.getenv("USER_KEVIN"), System.getenv("PASS_KEVIN"), "Kevin Rise"),
			User.create(System.getenv("USER_SCOTTRUM"), System.getenv("PASS_SCOTTRUM"), "Scott Rumrill"),
			User.create(System.getenv("USER_SCOTTSIM"), System.getenv("PASS_SCOTTSIM"), "Scott Simpson") };

	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public String showReset() {
		logger.info("Entering showReset");

		return "reset";
	}

	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String processReset(
			@RequestParam(value = "confirm", required = true) String confirm,
			@RequestParam(value = "primary", required = false) String primary,
			Model model) {
		logger.info("Entering processReset");

		Connection connect = null;
		PreparedStatement usersStatement = null;
		PreparedStatement listenersStatement = null;
		PreparedStatement blabsStatement = null;
		PreparedStatement commentsStatement = null;
		java.util.Date now = new java.util.Date();

		Random rand = new Random();

		recreateDatabaseSchema();

		try {
			logger.info("Getting Database connection");
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(Constants.create().getJdbcConnectionString());
			logger.info(connect);
			connect.setAutoCommit(false);

			logger.info("Preparing the Stetement for adding users");
			usersStatement = connect.prepareStatement(
					"INSERT INTO users (username, password, password_hint, created_at, last_login, real_name, blab_name) values (?, ?, ?, ?, ?, ?, ?);");
			for (int i = 0; i < users.length; i++) {
				logger.info("Adding user " + users[i].getUserName());
				usersStatement.setString(1, users[i].getUserName());
				usersStatement.setString(2, users[i].getPassword());
				usersStatement.setString(3, users[i].getPasswordHint());
				usersStatement.setTimestamp(4, users[i].getDateCreated());
				usersStatement.setTimestamp(5, users[i].getLastLogin());
				usersStatement.setString(6, users[i].getRealName());
				usersStatement.setString(7, users[i].getBlabName());

				usersStatement.executeUpdate();
			}
			connect.commit();

			logger.info("Preparing the Stetement for adding listeners");
			listenersStatement = connect
					.prepareStatement("INSERT INTO listeners (blabber, listener, status) values (?, ?, 'Active');");
			for (int i = 1; i < users.length; i++) {
				for (int j = 1; j < users.length; j++) {
					if (rand.nextBoolean() && i != j) {
						String blabber = users[i].getUserName();
						String listener = users[j].getUserName();

						logger.info("Adding " + listener + " as a listener of " + blabber);

						listenersStatement.setString(1, blabber);
						listenersStatement.setString(2, listener);

						listenersStatement.executeUpdate();
					}
				}
			}
			connect.commit();

			logger.info("Reading blabs from file");
			String[] blabsContent = loadFile("blabs.txt");

			logger.info("Preparing the Statement for adding blabs");
			blabsStatement = connect
					.prepareStatement("INSERT INTO blabs (blabber, content, timestamp) values (?, ?, ?);");
			for (String blabContent : blabsContent) {
				int randomUserOffset = rand.nextInt(users.length - 2) + 1;

				long vary = rand.nextInt(30 * 24 * 3600);

				String username = users[randomUserOffset].getUserName();
				logger.info("Adding a blab for " + username);

				blabsStatement.setString(1, username);
				blabsStatement.setString(2, blabContent);
				blabsStatement.setTimestamp(3, new Timestamp(now.getTime() - (vary * 1000)));

				blabsStatement.executeUpdate();
			}
			connect.commit();

			logger.info("Reading comments from file");
			String[] commentsContent = loadFile("comments.txt");

			logger.info("Preparing the Statement for adding comments");
			commentsStatement = connect.prepareStatement(
					"INSERT INTO comments (blabid, blabber, content, timestamp) values (?, ?, ?, ?);");
			for (int i = 1; i <= blabsContent.length; i++) {
				int count = rand.nextInt(6);

				for (int j = 0; j < count; j++) {
					int randomUserOffset = rand.nextInt(users.length - 2) + 1;
					String username = users[randomUserOffset].getUserName();

					int commentNum = rand.nextInt(commentsContent.length);
					String comment = commentsContent[commentNum];

					long vary = rand.nextInt(30 * 24 * 3600);

					logger.info("Adding a comment from " + username + " on blab ID " + String.valueOf(i));
					commentsStatement.setInt(1, i);
					commentsStatement.setString(2, username);
					commentsStatement.setString(3, comment);
					commentsStatement.setTimestamp(4, new Timestamp(now.getTime() - (vary * 1000)));

					commentsStatement.executeUpdate();
				}
			}
			connect.commit();
		} catch (SQLException | ClassNotFoundException ex) {
			logger.error(ex);
		} finally {
			try {
				if (usersStatement != null) {
					usersStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (listenersStatement != null) {
					listenersStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (blabsStatement != null) {
					blabsStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (commentsStatement != null) {
					commentsStatement.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
			try {
				if (connect != null) {
					connect.close();
				}
			} catch (SQLException exceptSql) {
				logger.error(exceptSql);
			}
		}

		return Utils.redirect("reset");
	}

	private void recreateDatabaseSchema() {
		logger.info("Reading database schema from file");
		String[] schemaSql = loadFile("blab_schema.sql", new String[] { "--", "/*" }, ";");

		Connection connect = null;
		Statement stmt = null;
		try {
			logger.info("Getting Database connection");
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(Constants.create().getJdbcConnectionString());

			stmt = connect.createStatement();

			for (String sql : schemaSql) {
				sql = sql.trim();
				if (!sql.isEmpty()) {
					logger.info("Executing: " + sql);
					System.out.println("Executing: " + sql);
					stmt.executeUpdate(sql);
				}
			}
		} catch (ClassNotFoundException | SQLException ex) {
			logger.error(ex);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException ex) {
				logger.error(ex);
			}
			try {
				if (connect != null) {
					connect.close();
				}
			} catch (SQLException ex) {
				logger.error(ex);
			}
		}
	}

	private String[] loadFile(String filename) {
		return loadFile(filename, new String[0], System.lineSeparator());
	}

	private String[] loadFile(String filename, String[] skipCharacters, String delimiter) {
		String path = "/app/src/main/resources" + File.separator + filename;

		String regex = "";
		if (skipCharacters.length > 0) {
			String skipString = String.join("|", skipCharacters);
			skipString = skipString.replaceAll("(?=[]\\[+&!(){}^\"~*?:\\\\])", "\\\\");
			regex = "^(" + skipString + ").*?";
		}

		String[] lines = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));

			String line = br.readLine();
			while (line != null) {
				if (line.matches(regex)) {
					line = br.readLine();
					continue;
				}

				sb.append(line);
				sb.append(System.lineSeparator());

				line = br.readLine();
			}

			lines = sb.toString().split(delimiter);
		} catch (IOException ex) {
			logger.error(ex);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				logger.error(ex);
			}
		}

		return lines;
	}
}