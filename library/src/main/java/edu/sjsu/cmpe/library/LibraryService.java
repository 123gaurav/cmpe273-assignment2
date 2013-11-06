package edu.sjsu.cmpe.library;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;
import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.dto.BookDto;
import edu.sjsu.cmpe.library.dto.BooksDto;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

public class LibraryService extends Service<LibraryServiceConfiguration> {
	
     
	private final Logger log = LoggerFactory.getLogger(getClass());
    public static String queueName;
    public static String topicName;
	public static String apolloUser;
	public static String apolloHost; 
	public static String apolloPort;
	public static String  host;
	public static String port;
	public static String user;
	public static String password;
	public static String destination;
	public static String destinationForTopic;
	public static String libraryName;
	public static String apolloPassword;
	public static BookRepositoryInterface bookRepository = new BookRepository();
	//private static String body;
    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
	int numThreads = 2;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    Runnable backgroundTask = new Runnable() {
    		 
	    @Override
	    public void run() {
		 try {
			listenMe();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	};
	System.out.println("About to submit the background task");
	executor.execute(backgroundTask);
	System.out.println("Submitted the background task");

	//executor.shutdown();
	System.out.println("Finished the background task");
    }
    

  protected static void listenMe() throws JMSException, MalformedURLException {

	String body=null;
	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
  	factory.setBrokerURI("tcp://" + host + ":" + port);
  	System.out.println(host);
  	Connection connection = factory.createConnection(user, password);
  	connection.start();
  	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  	Destination dest = new StompJmsDestination(topicName);
  	MessageConsumer consumer = session.createConsumer(dest);
  	System.currentTimeMillis();
  	System.out.println("Waiting for messages...");
  	
  	while(true) {
  	    Message msg = consumer.receive();
  	    System.out.println("In");
  	  if (msg instanceof TextMessage) {
  		TextMessage smsg = ((TextMessage) msg);
  		body=smsg.getText();
  		body=body.replace("\"", "");
  		System.out.println("the textmsg:"+ body);
        String [] myContent=body.split(":",4);
  		System.out.println(myContent[0]);
  		Book b=new Book();
  		Book b1=new Book();
  		//{isbn}:{title}:{category}:{coverimage}    # category
  		b.setIsbn(Long.parseLong(myContent[0]));
  		b.setTitle(myContent[1]);
  		b.setCategory(myContent[2]);
  		URL url = new URL(myContent[3]);
		b.setCoverimage(url);


  		Status stat=Status.valueOf("available");
  		b.setStatus(stat);
  		b1=bookRepository.saveBook(b);
  		System.out.println(b1);
  		System.out.println("Status of my book"+bookRepository.getBookByISBN(b.getIsbn()).getStatus().toString()+"ISBN value"+b.getIsbn());
  		if(bookRepository.getBookByISBN(b.getIsbn()).getStatus().toString()=="lost")
  		{
  			System.out.println("In lost");
  			System.out.println("status"+stat);
  			bookRepository.getBookByISBN(b.getIsbn()).setStatus(Status.available);
  		}
  		BookDto bookResponse = new BookDto(b1);

  		System.out.println("Received message = " + body);

  	    } 
  	else {
  		System.out.println("Unexpected message type: "+msg.getClass());
  	    }
  	
  	}
  	
		// TODO Auto-generated method stub
		
	}



	@Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap) {
	bootstrap.setName("library-service");
	bootstrap.addBundle(new ViewBundle());
	bootstrap.addBundle(new AssetsBundle());
    }
    
    private static String env(String key, String defaultValue) {
    	String rc = System.getenv(key);
    	if( rc== null ) {
    	    return defaultValue;
    	}
    	return rc;
        }

    @Override
    public void run(LibraryServiceConfiguration configuration,
	    Environment environment) throws Exception {
	// This is how you pull the configurations from library_x_config.yml
	  queueName = configuration.getStompQueueName();
	  topicName = configuration.getStompTopicName();
	  log.debug("{} - Queue name is {}. Topic name is {}",
		configuration.getLibraryName(), queueName,
		topicName);
		queueName = configuration.getStompQueueName();
		apolloHost=configuration.getApolloHost();
		apolloPort=configuration.getApolloPort();
		libraryName=configuration.getLibraryName();
		apolloUser=configuration.getApolloUser();
		apolloPassword=configuration.getApolloPassword();
		host =env("APOLLO_HOST", apolloHost);
		port = env("APOLLO_PORT", apolloPort);
		user = env("APOLLO_USER", apolloUser);
		password = env("APOLLO_PASSWORD", apolloPassword);
		destination = configuration.getStompQueueName();	
	// TODO: Apollo STOMP Broker URL and login

	/** Root API */
	environment.addResource(RootResource.class);
	/** Books APIs */
	
	environment.addResource(new BookResource(bookRepository));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
    }
}
