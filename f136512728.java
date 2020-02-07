import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.TextArea;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.PreparedStatement;

import net.proteanit.sql.DbUtils;

import java.awt.Color;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionListener;
import javax.swing.JRadioButton;

public class GUI extends ConnectDatabase {

	private JFrame frame;
	private JTextField txtSearch;
	private JButton btnSearch;
	private JTable table;
	private final Action action = new checkOutAction();
	private JScrollPane scrollPane;
	private final Action action_1 = new checkInAction();
	private JRadioButton rdbtnSearchByAuthor;

	// Boolean to help with selecting a user. We don't want to search for a book and
	// have that book title be selected as a user.
	// Only have the ability to select a user if in the patrons table
	boolean inPatron = false;
	int helpingUserId = 0;
	String helpingUserName = "";

	int selectedBook;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return;
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Dialog", Font.BOLD, 18));
		frame.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});
		frame.setBounds(100, 100, 952, 573);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		txtSearch = new JTextField();
		txtSearch.setBounds(148, 27, 312, 19);
		txtSearch.setText("Search");
		txtSearch.setColumns(10);

		btnSearch = new JButton("Search");

		
		btnSearch.setBounds(490, 24, 83, 25);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(txtSearch);
		frame.getContentPane().add(btnSearch);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(85, 88, 785, 369);
		frame.getContentPane().add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.BLACK, null, null, null));

		JButton btnCheckout = new JButton("Check Out");
		btnCheckout.setEnabled(false);
		btnCheckout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnCheckout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				System.out.println("Check Out Pressed");

				// if a user is not signed in, prompt to sign in and then return.
				if (helpingUserId == 0) {
					System.out.println("No user is signed in. Please sign in and try again");
					return;
				}


				String query = "UPDATE Patron set BorrowingId = ? " + " WHERE PatronId = ?";

				try {

					PreparedStatement pst = (PreparedStatement) connection.prepareStatement(query);

					pst.setInt(1, selectedBook);
					pst.setInt(2, helpingUserId);

					pst.executeUpdate();

					ConnectDatabase.main(null);

				} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {

					System.out.println(e1.getMessage());

				}

			}
		});
		btnCheckout.setAction(action);
		btnCheckout.setBounds(753, 481, 117, 25);
		frame.getContentPane().add(btnCheckout);

		JButton btnCheckin = new JButton("Check In");
		btnCheckin.setEnabled(false);
		btnCheckin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				System.out.println("Check In Pressed");

				// if a user is not signed in, prompt to sign in and then return.
				if (helpingUserId == 0) {
					System.out.println("No user is signed in. Please sign in and try again");
					return;
				}


				String query = "UPDATE Patron set BorrowingId = NULL " + " WHERE PatronId = ? HAVING BorrowingId !ISNULL";

				try {

					PreparedStatement pst = (PreparedStatement) connection.prepareStatement(query);

					pst.setInt(1, helpingUserId);

					pst.executeUpdate();

					ConnectDatabase.main(null);


				} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {

					System.out.println(e1.getMessage());

				}

			}
		});

		btnCheckin.setAction(action_1);
		btnCheckin.setBounds(621, 481, 117, 25);
		frame.getContentPane().add(btnCheckin);

		// Set the JRadioButton search by title to true as the default option
		JRadioButton rdbtnSearchByTitle = new JRadioButton("Search by title", true);

		rdbtnSearchByTitle.setFont(new Font("Dialog", Font.BOLD, 15));
		rdbtnSearchByTitle.setBounds(148, 54, 149, 23);
		frame.getContentPane().add(rdbtnSearchByTitle);

		JRadioButton rdbtnSearchByGenre = new JRadioButton("Search by genre", false);

		rdbtnSearchByGenre.setFont(new Font("Dialog", Font.BOLD, 15));
		rdbtnSearchByGenre.setBounds(302, 54, 159, 23);
		frame.getContentPane().add(rdbtnSearchByGenre);

		rdbtnSearchByAuthor = new JRadioButton("Search by author");

		rdbtnSearchByAuthor.setFont(new Font("Dialog", Font.BOLD, 15));
		rdbtnSearchByAuthor.setBounds(476, 57, 204, 23);
		frame.getContentPane().add(rdbtnSearchByAuthor);

		rdbtnSearchByTitle.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Search by Title selected");
				rdbtnSearchByTitle.setSelected(true);
				rdbtnSearchByGenre.setSelected(false);
				rdbtnSearchByAuthor.setSelected(false);

				return;

			}
		});

		rdbtnSearchByGenre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Search by Genre selected");
				rdbtnSearchByTitle.setSelected(false);
				rdbtnSearchByGenre.setSelected(true);
				rdbtnSearchByAuthor.setSelected(false);

				return;

			}
		});

		rdbtnSearchByAuthor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Search by Author selected");
				rdbtnSearchByTitle.setSelected(false);
				rdbtnSearchByGenre.setSelected(false);
				rdbtnSearchByAuthor.setSelected(true);

				return;

			}
		});

		// Use HTML format to correctly create a new line in the jlabel field. This
		// jlabel shows the patron being helped or selected
		JLabel lblCurrentPatron = new JLabel("<html>Current Patron<br/>" + helpingUserName + "</html>",
				SwingConstants.CENTER);

		lblCurrentPatron.setFont(new Font("Dialog", Font.BOLD, 16));
		lblCurrentPatron.setBounds(694, 27, 189, 47);
		// Allow enter button to launch search button, along with mouse click
		frame.getRootPane().setDefaultButton(btnSearch);

		JButton btnSignOutPatron = new JButton("Sign out Patron");

		// The sign out patron button will be grayed out unless a patron is selected
		btnSignOutPatron.setEnabled(false);
		btnSignOutPatron.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Signing off " + helpingUserName);

				helpingUserName = "";
				lblCurrentPatron.setText("<html>Current Patron<br/>" + helpingUserName + "</html>");

				// The sign out patron button will be grayed out unless a patron is selected
				btnSignOutPatron.setEnabled(false);

				try {
					ConnectDatabase.main(null);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		btnSignOutPatron.setBounds(432, 487, 149, 25);
		frame.getContentPane().add(btnSignOutPatron);

		JButton btnPatrons = new JButton("Patrons");

		btnPatrons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// scrollPane.setVisible(true);

				// Pull up a list of all patrons

				// return if the patrons table is already up. Else, this results in crashing
				// without this if statement
				if (inPatron) {
					return;
				}

				inPatron = true;

				try {


					btnSearch.setEnabled(false);
					txtSearch.setEnabled(false);

					System.out.println("Searching database for patrons");

					// query that will search the database for title of a book or name of a genre
					String query = "SELECT PatronId, " + 
							"       REVERSE(SUBSTRING_INDEX(REVERSE(name), ' ', 1)) AS LastName," + 
							"       TRIM(SUBSTRING(name, 1, CHAR_LENGTH(name) - CHAR_LENGTH(SUBSTRING_INDEX(REVERSE(name), ' ', 1)))) AS FirstName," + 
							"       CASE " + 
							"			WHEN BorrowingId IS NULL THEN 'No books borrowed' ELSE BorrowingId" + 
							"" + 
							"			END AS 'Book Borrowed'" + 
							"" + 
							"	FROM Patron" + 
							"    ORDER BY LastName ASC";


					PreparedStatement pst = (PreparedStatement) connection.prepareStatement(query);

					TimeUnit.SECONDS.sleep(1);

					ResultSet rs = pst.executeQuery();

					TimeUnit.SECONDS.sleep(1);

					table.setModel(DbUtils.resultSetToTableModel(rs));

					return;

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				// do some actions here, for example
				// print first column value from selected row
				// System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString());
				if (inPatron) {

					System.out.println("In patron right now");

					helpingUserId = (int) (table.getValueAt(table.getSelectedRow(), 0));
					helpingUserName = (String) (table.getValueAt(table.getSelectedRow(), 2));
					helpingUserName += " " + (String) (table.getValueAt(table.getSelectedRow(), 1));


					System.out.println("Now helping user " + helpingUserName + " user id " + helpingUserId);
					lblCurrentPatron.setText("<html>Current Patron<br/>" + helpingUserName + "</html>");

					btnSignOutPatron.setEnabled(true);

					return;
				}

				// else, user must be in the book list. Clicking a row in the book list produces
				// more info about
				// the book. In this form, books can be checked in or checked out
				else {
					System.out.println("In booklist right now");
					// scrollPane.setVisible(false);
					// String checkBook;

					// checkBook = (String) (table.getValueAt(table.getSelectedRow(), 1));
					// System.out.println("Selected book " + checkBook);

					// don't want the book ID to appear in the GUI, so search for it with a query
					selectedBook = (int) (table.getValueAt(table.getSelectedRow(), 5));
					System.out.println("Selected bookId " + selectedBook);

					return;

				}
			}
		});

		btnPatrons.setBounds(235, 469, 173, 43);
		frame.getContentPane().add(btnPatrons);

		frame.getContentPane().add(lblCurrentPatron);

		JButton btnBookList = new JButton("Book List");
		btnBookList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				inPatron = false;

				try {

					btnSearch.setEnabled(true);
					txtSearch.setEnabled(true);
					
					btnCheckin.setEnabled(true);
					btnCheckout.setEnabled(true);

					// query that will search the database for title of a book or name of a genre
					String query = "SELECT Title, Author, Genre.GenreName as Genre, Year, Publisher.Name as Publisher, BookId" + 
									" FROM Books JOIN Genre ON Books.GenreId = Genre.GenreId" + 
									" JOIN Publisher ON Books.PublisherId = Publisher.PublisherId" + 
									" ORDER BY Title ASC";
							
//

					PreparedStatement pst = (PreparedStatement) connection.prepareStatement(query);

					ResultSet rs = pst.executeQuery();
					
					table.setModel(DbUtils.resultSetToTableModel(rs));

					return;

				}

				catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnBookList.setBounds(85, 469, 117, 43);
		frame.getContentPane().add(btnBookList);

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// this string will hold the text in the search bar
				String searchText;

				// scrollPane.setVisible(true);

				// searching in the Book List
				try {

					// This string will determine if the query will search by title or genre in the
					// search bar, based upon JRadioBox selections
					String searchTerm;

					if (rdbtnSearchByTitle.isSelected()) {
						System.out.println("Search by title is selected");
						searchTerm = "Title";

					} else if (rdbtnSearchByAuthor.isSelected()) {
						System.out.println("Search by author is selected");
						searchTerm = "Author";
					} else {
						System.out.println("Search by genre is selected");
						searchTerm = "Genre.GenreName";
					}
					searchText = txtSearch.getText();
					System.out.println("Searching database for title " + searchText);

					// query that will search the database for title of a book or name of a genre
					String query = "SELECT Title, Author, Genre.GenreName as Genre, Year, Publisher.Name as Publisher, BookId" + 
									" FROM Books JOIN Genre ON Books.GenreId = Genre.GenreId" + 
									" JOIN Publisher ON Books.PublisherId = Publisher.PublisherId" +
									" WHERE " + searchTerm + " LIKE '%" + searchText + "%'" + 
									" ORDER BY Title ASC";


					PreparedStatement pst = (PreparedStatement) connection.prepareStatement(query);
					TimeUnit.SECONDS.sleep(1);

					ResultSet rs = pst.executeQuery();
					TimeUnit.SECONDS.sleep(1);

					table.setModel(DbUtils.resultSetToTableModel(rs));

					return;

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	private class checkOutAction extends AbstractAction {
		public checkOutAction() {
			putValue(NAME, "Check Out");
			putValue(SHORT_DESCRIPTION,
					"Check out a selected book. Book must not be currently checked out. A user must be selected to check a book out");

		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class checkInAction extends AbstractAction {
		public checkInAction() {
			putValue(NAME, "Check In");
			putValue(SHORT_DESCRIPTION,
					"Check in a selected book. Book must not be currently checked in. A user does not need to be selected to check a book in");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
}
