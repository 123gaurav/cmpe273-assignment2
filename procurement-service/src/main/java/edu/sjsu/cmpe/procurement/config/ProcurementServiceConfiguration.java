package edu.sjsu.cmpe.procurement.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class ProcurementServiceConfiguration extends Configuration {
	
	@NotEmpty
    @JsonProperty
    public static String apolloUser;
	
	@NotEmpty
    @JsonProperty
    public static String apolloPassword;
	
	@NotEmpty
    @JsonProperty
    public static String apolloHost;
	
	@NotEmpty
    @JsonProperty
    public static String apolloPort;
	
    @NotEmpty
    @JsonProperty
    private String stompQueueName;

    @NotEmpty
    @JsonProperty
    private String stompTopicPrefix;

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    /**
     * 
     * @return
     */
    public JerseyClientConfiguration getJerseyClientConfiguration() {
	return httpClient;
    }

    /**
     * @return the stompQueueName
     */
    public String getStompQueueName() {
	return stompQueueName;
    }

    /**
     * @param stompQueueName
     *            the stompQueueName to set
     */
    public void setStompQueueName(String stompQueueName) {
	this.stompQueueName = stompQueueName;
    }

    public String getStompTopicPrefix() {
	return stompTopicPrefix;
    }

    public void setStompTopicPrefix(String stompTopicPrefix) {
	this.stompTopicPrefix = stompTopicPrefix;
    }

	public String getApolloUser() {
		return apolloUser;
	}

	@SuppressWarnings("static-access")
	public void setApolloUser(String apolloUser) {
		this.apolloUser = apolloUser;
	}

	public String getApolloPassword() {
		return apolloPassword;
	}

	@SuppressWarnings("static-access")
	public void setApolloPassword(String apolloPassword) {
		this.apolloPassword = apolloPassword;
	}

	public String getApolloHost() {
		return apolloHost;
	}

	@SuppressWarnings("static-access")
	public void setApolloHost(String apolloHost) {
		this.apolloHost = apolloHost;
	}

	public String getApolloPort() {
		return apolloPort;
	}

	@SuppressWarnings("static-access")
	public void setApolloPort(String apolloPort) {
		this.apolloPort = apolloPort;
	}   

}
