package com.mycompany.instaface;

import javax.swing.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextField;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;

public class InstaFace extends JFrame
{
    User User;
    String Target = "http://localhost:8080/InstaFaceRestServer/api/restserver/"; // Το βασικό Target URL που χρησιμοποιούμε.
    Client Client = ClientBuilder.newClient();
    
    JLabel UsernameLabel = new JLabel("", SwingConstants.CENTER);
    JLabel NameSurnameLabel = new JLabel("", SwingConstants.CENTER);
    
    JPanel LogPanel = new JPanel(new GridLayout(0, 4));
    JPanel MainPanel = new JPanel(new GridLayout(0, 4));

    public InstaFace()
    {
        super("InstaFace");
        InitializeFrame();
    }
    
    public void InitializeFrame()
    {
        CreateLogPanel();
        CreateMainPanel();
        
        setSize(400, 100);
        setBackground(Color.GRAY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        add(LogPanel);
        setVisible(true);
    }
    
    public void CreateLogPanel()
    {
        JButton SignUpButton = new JButton("Sign Up");
        TextField NameTextField = new TextField();
        TextField SurnameTextField = new TextField();
        TextField UsernameTextField = new TextField();
        JPasswordField PasswordField = new JPasswordField();
        JRadioButton MaleRadioButton = new JRadioButton("Male");
        JRadioButton FemaleRadioButton = new JRadioButton("Female");
        TextField DescriptionTextField = new TextField();
        TextField CountryTextField = new TextField();
        TextField TownTextField = new TextField();
        JTextField UsernameSignIn = new JTextField();
        JPasswordField PasswordSignIn = new JPasswordField();
        JButton SignInButton = new JButton("Sign In");
        
        SignUpButton.setFocusable(false);
        SignUpButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                MaleRadioButton.addMouseListener(new java.awt.event.MouseAdapter()
                {
                    public void mouseClicked(java.awt.event.MouseEvent evt)
                    {
                        if(MaleRadioButton.isSelected() && FemaleRadioButton.isSelected())
                            FemaleRadioButton.setSelected(false);
                    }
                });
                
                FemaleRadioButton.addMouseListener(new java.awt.event.MouseAdapter()
                {
                    public void mouseClicked(java.awt.event.MouseEvent evt)
                    {
                        if(FemaleRadioButton.isSelected() && MaleRadioButton.isSelected())
                            MaleRadioButton.setSelected(false);
                    }
                });     

                JPanel SignUpPanel = new JPanel(new GridLayout(0, 2));
                SignUpPanel.add(new JLabel("Name"));
                SignUpPanel.add(NameTextField);
                SignUpPanel.add(new JLabel("Surname"));
                SignUpPanel.add(SurnameTextField);
                SignUpPanel.add(new JLabel("Username"));
                SignUpPanel.add(UsernameTextField);
                SignUpPanel.add(new JLabel("Password"));
                SignUpPanel.add(PasswordField);
                SignUpPanel.add(MaleRadioButton);
                SignUpPanel.add(FemaleRadioButton);
                SignUpPanel.add(new JLabel("Description"));
                SignUpPanel.add(DescriptionTextField);
                SignUpPanel.add(new JLabel("Country"));
                SignUpPanel.add(CountryTextField);
                SignUpPanel.add(new JLabel("Town"));
                SignUpPanel.add(TownTextField);
                
                NameTextField.setText("");
                SurnameTextField.setText("");
                UsernameTextField.setText("");
                PasswordField.setText("");
                MaleRadioButton.setSelected(false);
                FemaleRadioButton.setSelected(false);
                DescriptionTextField.setText("");
                CountryTextField.setText("");
                TownTextField.setText("");
                    
                String name;
                String surname;
                String username;
                char[] password;
                int genre;
                String description;
                String country;
                String town;
                
                boolean valid;
                do
                {
                    Object []options= {"Sign Up", "Cancel"};
                    int option = JOptionPane.showOptionDialog(null, SignUpPanel, "Sign Up", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, options, null);
                    if(option == 0)
                    {
                        name = NameTextField.getText();
                        surname = SurnameTextField.getText();
                        username = UsernameTextField.getText();
                        password = PasswordField.getPassword();
                        description = DescriptionTextField.getText();
                        country = CountryTextField.getText();
                        town = TownTextField.getText();

                        valid = true;
                        if(name.length() == 0
                            || surname.length() == 0
                            || username.length() == 0
                            || password.length == 0
                            || (!MaleRadioButton.isSelected() && !FemaleRadioButton.isSelected())
                            || country.length() == 0
                            || town.length() == 0)
                        {
                            JOptionPane.showMessageDialog(null, "Please enter your credentials!");
                            valid = false;
                        }
                    }
                    else return;
                } while(!valid);
                
                if(MaleRadioButton.isSelected())
                    genre = 1;
                else
                    genre = 2;
                
                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να κάνει εγγραφή.
                WebTarget target = Client.target(Target + "signup");
                // Του στέλνω σε json τα στοιχεία του χρήστη για να εγγράψει το χρήστη αν όλα είναι εντάξει.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(new User(name, surname, username, Hash(password), genre, description, country, town).ToJSON()));

                if (response.getStatus() == 200)
                    JOptionPane.showMessageDialog(null, "Successful sign up!");
                else if (response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "Username already exists!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");
            }
        });
        
        SignInButton.setFocusable(false);
        SignInButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                String username = UsernameSignIn.getText();
                char[] password = PasswordSignIn.getPassword();

                if(username.length() == 0 || password.length == 0)
                {
                    JOptionPane.showMessageDialog(null, "Please enter your credentials!");
                    return;
                }

                User = new User(username, Hash(password));
                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να κάνει είσοδο.
                WebTarget target = Client.target(Target + "signin");
                // Του στέλνω σε json τα στοιχεία του χρήστη για να ελέγξει αν ο χρήστης υπάρχει για να κάνει είσοδο.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(User.ToJSON()));

                if (response.getStatus() == 200)
                {
                    UsernameSignIn.setText("");
                    PasswordSignIn.setText("");
                    
                    setSize(600, 200);
                    remove(LogPanel);
                    
                    // Αν ο χρήστης υπάρχει, το web app επιστρέφει όλα τα στοιχεία του και τα κρατάμε σε μια μεταβλητή User.
                    JSONObject json = new JSONObject(response.readEntity(String.class));
                    User = new User(json.getString("Name"), json.getString("Surname"), json.getString("Username"), json.getString("Password"), json.getInt("Genre"), json.getString("Description"), json.getString("Country"), json.getString("Town"));
                    NameSurnameLabel.setText(User.getName() + " " + User.getSurname());
                    UsernameLabel.setText(User.getUsername());
                    add(MainPanel);
                }
                else if (response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "Wrong username or password!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");
            }
        });

        LogPanel.setLayout(new GridLayout(0, 4));
        LogPanel.add(new JLabel("Username", SwingConstants.CENTER));
        LogPanel.add(new JLabel("Password", SwingConstants.CENTER));
        LogPanel.add(new JLabel());
        LogPanel.add(SignUpButton);
        LogPanel.add(UsernameSignIn);
        LogPanel.add(PasswordSignIn);
        LogPanel.add(SignInButton);
    }
    
    public void CreateMainPanel()
    {
        JButton SignOutButton = new JButton("Sign Out");
        JTextField AddFriendTextField = new JTextField();
        JButton AddFriendButton = new JButton("Add");
        JButton ShowFriendsButton = new JButton("Friends");
        JTextField DeleteFriendTextField = new JTextField();
        JButton DeleteFriendButton = new JButton("Delete");
        JTextField EditPostTextField = new JTextField();
        JButton EditPostButton = new JButton("Edit");
        JButton ShowPostsButton = new JButton("Posts");
        JTextField DeletePostTextField = new JTextField();
        JButton DeletePostButton = new JButton("Delete");
        JButton CreatePostButton = new JButton("Create Post");

        SignOutButton.setFocusable(false);
        SignOutButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                setSize(400, 100);
                remove(MainPanel);
                add(LogPanel);
            }
        });
        
        AddFriendButton.setFocusable(false);
        AddFriendButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                String friend = AddFriendTextField.getText();
        
                if(friend.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Field is empty!");
                    return;
                }

                if(friend.equals(User.getUsername()))
                {
                    JOptionPane.showMessageDialog(null, "Wrong input!");
                    return;
                }

                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να κάνει προσθέσει ένα φίλο.
                WebTarget target = Client.target(Target + "addfriend");
                // Του στέλνω σε json τα στοιχεία του χρήστη και του φίλου που θέλει να προσθέσει για να δημιουεγήσει τη φιλία.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(new Friendship(User.getUsername(), friend).ToJSON()));

                if (response.getStatus() == 200)
                    JOptionPane.showMessageDialog(null, "Friend added successfully!");
                else if (response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "Already friends with user " + friend + "!");
                else if (response.getStatus() == 202)
                    JOptionPane.showMessageDialog(null, "User " + friend + " does not exist!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");

                AddFriendTextField.setText("");
            }
        });
        
        ShowFriendsButton.setFocusable(false);
        ShowFriendsButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να επιστρέψει τους φίλους του χρήστη.
                WebTarget target = Client.target(Target + "friends");
                // Του στέλνω σε json τα στοιχεία του χρήστη για να μας επιστρέψει σε ενα string με τους φίλους του.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(User.ToJSON()));

                if (response.getStatus() == 200)
                {
                    String[] friends = response.readEntity(String.class).split("\n");
                    JPanel ShowFriendsPanel = new JPanel(new GridLayout(0, 7));
                    ShowFriendsPanel.add(new JLabel("Name", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel("Surname", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel("Username", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel("Genre", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel("Description", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel("Country", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel("Town", SwingConstants.CENTER));
                    ShowFriendsPanel.add(new JLabel());
                    ShowFriendsPanel.add(new JLabel());
                    ShowFriendsPanel.add(new JLabel());
                    ShowFriendsPanel.add(new JLabel());
                    ShowFriendsPanel.add(new JLabel());
                    ShowFriendsPanel.add(new JLabel());
                    ShowFriendsPanel.add(new JLabel());
                    
                    for(String friend : friends)
                    {
                        JSONObject json  = new JSONObject(friend);
                        User user = new User(json.getString("Name"), json.getString("Surname"), json.getString("Username"), json.getString("Password"), json.getInt("Genre"), json.getString("Description"), json.getString("Country"), json.getString("Town"));
                        ShowFriendsPanel.add(new JLabel(user.getName(), SwingConstants.CENTER));
                        ShowFriendsPanel.add(new JLabel(user.getSurname(), SwingConstants.CENTER));
                        ShowFriendsPanel.add(new JLabel(user.getUsername(), SwingConstants.CENTER));
                        ShowFriendsPanel.add(new JLabel(user.getGenreStr(), SwingConstants.CENTER));
                        ShowFriendsPanel.add(new JLabel(user.getDescription(), SwingConstants.CENTER));
                        ShowFriendsPanel.add(new JLabel(user.getCountry(), SwingConstants.CENTER));
                        ShowFriendsPanel.add(new JLabel(user.getTown(), SwingConstants.CENTER));
                    }
                    
                    JOptionPane.showConfirmDialog(null, ShowFriendsPanel, "Friends", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
                else if(response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "There are no users in your friendlist!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");
            }
        });
        
        DeleteFriendButton.setFocusable(false);
        DeleteFriendButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                String friend = DeleteFriendTextField.getText();
        
                if(friend.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Field is empty!");
                    return;
                }

                if(friend.equals(User.getUsername()))
                {
                    JOptionPane.showMessageDialog(null, "Wrong input!");
                    return;
                }

                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να διαγράψει μια φιλία.
                WebTarget target = Client.target(Target + "deletefriend");
                // Του στέλνω σε json τα στοιχεία του χρήστη και του φίλου του διαγράψει τη φιλία.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(new Friendship(User.getUsername(), friend).ToJSON()));

                if (response.getStatus() == 200)
                    JOptionPane.showMessageDialog(null, "Friend deleted successfully!");
                else if (response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "Not friends with user " + friend + "!");
                else if (response.getStatus() == 202)
                    JOptionPane.showMessageDialog(null, "User " + friend + " does not exist!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");

                DeleteFriendTextField.setText("");
            }
        });
        
        EditPostButton.setFocusable(false);
        EditPostButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                String postid = EditPostTextField.getText();;

                if(postid.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Field is empty!");
                    return;
                }
                
                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να ελέγξει αν το Post υπάρχει.
                WebTarget target = Client.target(Target + "getpost");
                // Του στέλνω σε string το Id του Post και επιστρέφει σε json όλα τα στοιχεία του Post.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(postid));
                
                if (response.getStatus() == 200)
                {
                    JSONObject json = new JSONObject(response.readEntity(String.class));
                    Post post = new Post(json.getInt("Id"), json.getString("User1"), json.getString("User2"), json.getString("Text"));
                    JPanel EditPostPanel = new JPanel(new GridLayout(0, 2));
                    JTextField PostTextField = new JTextField(post.getText());
                    EditPostPanel.add(new JLabel("Text", SwingConstants.CENTER));
                    EditPostPanel.add(PostTextField);

                    boolean valid;
                    do
                    {
                        Object []options= {"Save", "Cancel"};
                        int option = JOptionPane.showOptionDialog(null, EditPostPanel, "Edit Post", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, options, null);
                        if(option == 0)
                        {
                            String text = PostTextField.getText();

                            valid = true;
                            if(text.length() == 0)
                            {
                                JOptionPane.showMessageDialog(null, "Field is empty!");
                                valid = false;
                            }
                            else
                            {
                                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να κάνει Update το Post.
                                target = Client.target(Target + "editpost");
                                post.setText(text);
                                // Του στέλνω σε json το Post με το νέο περιεχόμενο.
                                response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(post.ToJSON_ID()));

                                if (response.getStatus() == 200)
                                    JOptionPane.showMessageDialog(null, "Post saved successfully!");
                                else
                                    JOptionPane.showMessageDialog(null, "Something went wrong!");
                            }
                        }
                        else return;
                    } while(!valid);
                }
                else if(response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "There is no post with id " + postid + "!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");
            }
        });
        
        ShowPostsButton.setFocusable(false);
        ShowPostsButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να εμφανίσει τα Post του χρήστη.
                WebTarget target = Client.target(Target + "posts");
                // Του στέλνω σε json τον χρήστη και μου επιστρέφει σε ένα string και σε json όλα τα Post που έχει κάνει.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(User.ToJSON()));

                if (response.getStatus() == 200)
                {
                    String[] posts = response.readEntity(String.class).split("\n");
                    JPanel ShowPostsPanel = new JPanel(new GridLayout(0, 3));
                    ShowPostsPanel.add(new JLabel("Id", SwingConstants.CENTER));
                    ShowPostsPanel.add(new JLabel("Friend", SwingConstants.CENTER));
                    ShowPostsPanel.add(new JLabel("Text", SwingConstants.CENTER));
                    ShowPostsPanel.add(new JLabel());
                    ShowPostsPanel.add(new JLabel());
                    ShowPostsPanel.add(new JLabel());
                    
                    for(String p : posts)
                    {
                        JSONObject json  = new JSONObject(p);
                        Post post = new Post(json.getInt("Id"), json.getString("User1"), json.getString("User2"), json.getString("Text"));
                        ShowPostsPanel.add(new JLabel(Integer.toString(post.getId()), SwingConstants.CENTER));
                        ShowPostsPanel.add(new JLabel(post.getUser2(), SwingConstants.CENTER));
                        ShowPostsPanel.add(new JLabel(post.getText(), SwingConstants.CENTER));
                    }
                    
                    JOptionPane.showConfirmDialog(null, ShowPostsPanel, "Posts", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
                else if(response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "There are no posts!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");
            }
        });
        
        DeletePostButton.setFocusable(false);
        DeletePostButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                String postid = DeletePostTextField.getText();
        
                if(postid.length() == 0)
                {
                    JOptionPane.showMessageDialog(null, "Field is empty!");
                    return;
                }

                // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να διαγράψει ένα Post.
                WebTarget target = Client.target(Target + "deletepost");
                // Του στέλνω σε string το Id του Post για να διαγράψει το Post αν υπάρχει.
                Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(postid));

                if (response.getStatus() == 200)
                    JOptionPane.showMessageDialog(null, "Post deleted successfully!");
                else if (response.getStatus() == 201)
                    JOptionPane.showMessageDialog(null, "There is no post with id " + postid + "!");
                else
                    JOptionPane.showMessageDialog(null, "Something went wrong!");

                DeletePostTextField.setText("");
            }
        });
        
        CreatePostButton.setFocusable(false);
        CreatePostButton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                JPanel CreatePostPanel = new JPanel(new GridLayout(0, 2));
                JTextField FriendTextField = new JTextField();
                JTextField PostTextField = new JTextField();
                CreatePostPanel.add(new JLabel("Friend Username", SwingConstants.CENTER));
                CreatePostPanel.add(FriendTextField);
                CreatePostPanel.add(new JLabel("Text", SwingConstants.CENTER));
                CreatePostPanel.add(PostTextField);
                
                String username;
                String text;
                
                boolean valid;
                do
                {
                    Object []options= {"Post", "Cancel"};
                    int option = JOptionPane.showOptionDialog(null, CreatePostPanel, "Create Post", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, options, null);
                    if(option == 0)
                    {
                        username = FriendTextField.getText();
                        text = PostTextField.getText();

                        valid = true;
                        if(username.length() == 0 || text.length() == 0)
                        {
                            JOptionPane.showMessageDialog(null, "Please fill all the fields!");
                            valid = false;
                        }
                        else if(username.equals(User.getUsername()))
                        {
                            JOptionPane.showMessageDialog(null, "Wrong input!");
                            valid = false;
                        }
                        else
                        {
                            // Θέτω target το αρχικό URL και του προσθέτω σε ποιο path του web app θα στέλνει για να δημιουργήσει ένα Post.
                            WebTarget target = Client.target(Target + "createpost");
                            Post post = new Post(User.getUsername(), username, text);
                            // Του στέλνω σε json το Post που επιθυμώ να κάνω με όλα τα απαραίτητα στοιχεία.
                            Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.json(post.ToJSON()));

                            if (response.getStatus() == 200)
                                JOptionPane.showMessageDialog(null, "Post created successfully!");
                            else if(response.getStatus() == 201)
                            {
                                JOptionPane.showMessageDialog(null, "There is no user with username " + username + "!");
                                valid = false;
                            }
                            else if(response.getStatus() == 202)
                            {
                                JOptionPane.showMessageDialog(null, "You are not friends with user " + username +  "!");
                                valid = false;
                            }
                            else
                                JOptionPane.showMessageDialog(null, "Something went wrong!");
                        }
                    }
                    else return;
                } while(!valid);
            }
        });
        
        MainPanel.add(NameSurnameLabel);
        MainPanel.add(UsernameLabel);
        MainPanel.add(new JLabel());
        MainPanel.add(SignOutButton);
        MainPanel.add(new JLabel("Add Friend", SwingConstants.CENTER));
        MainPanel.add(AddFriendTextField);
        MainPanel.add(AddFriendButton);
        MainPanel.add(ShowFriendsButton);
        MainPanel.add(new JLabel("Delete Friend", SwingConstants.CENTER));
        MainPanel.add(DeleteFriendTextField);
        MainPanel.add(DeleteFriendButton);
        MainPanel.add(new JLabel());
        MainPanel.add(new JLabel("Edit Post", SwingConstants.CENTER));
        MainPanel.add(EditPostTextField);
        MainPanel.add(EditPostButton);
        MainPanel.add(ShowPostsButton);
        MainPanel.add(new JLabel("Delete Post", SwingConstants.CENTER));
        MainPanel.add(DeletePostTextField);
        MainPanel.add(DeletePostButton);
        MainPanel.add(new JLabel());
        MainPanel.add(new JLabel());
        MainPanel.add(new JLabel());
        MainPanel.add(CreatePostButton);
    }
    
    // Κρυπτογράφηση στον κωδικό του χρήστη.
    public String Hash(char[] passwordArr)
    {
        try
        {
            String password = "";
            for(char c : passwordArr)
                password += c;
            
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(password.getBytes("UTF-8"), 0, password.length());
            return DatatypeConverter.printHexBinary(msdDigest.digest());
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e)
        {
            System.err.println("Something went wrong (HashPassword)!");
        }
        
        return null;
    }
}

