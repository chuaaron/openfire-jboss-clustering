/**
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
**/
package com.enernoc.rnd.openfire.cluster;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.cluster.ClusterManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enernoc.rnd.openfire.cluster.session.ClusteredSessionLocator;

public class JBossClusterPlugin implements Plugin {

	protected final Logger log = LoggerFactory.getLogger( getClass() );
	
	public static final String CLUSTER_JGROUPS_CONFIG_PROPERTY = 
		"com.enernoc.clustering.jgroups.config";
	public static final String CLUSTER_CACHE_CONFIG_PROPERTY = 
		"com.enernoc.clustering.cache.config";
	
	
	public void destroyPlugin() {
		log.info("Destroying the plugin");
		ClusterManager.shutdown();
		// TODO interrupt master listener & node listener?
		// TODO destroy all singletons and resources associated with this plugin
		
	}

	public void initializePlugin( PluginManager mgr, File pluginDir ) {
		try {
			log.debug(new String(Inet4Address.getLocalHost().getAddress()));
		} catch (UnknownHostException e) {
			log.debug("unable to obtain ipaddress for host");
		}
		LogManager.getLogManager().getLogger("").setLevel(Level.FINE);
		Enumeration<String> es = LogManager.getLogManager().getLoggerNames();
		while ( es.hasMoreElements() ) log.error( es.nextElement() );
		XMPPServer.getInstance().setRemoteSessionLocator( new ClusteredSessionLocator() );
		XMPPServer.getInstance().getRoutingTable().setRemotePacketRouter( new ClusterPacketRouter() );
		ClusterManager.startup();
	}

}