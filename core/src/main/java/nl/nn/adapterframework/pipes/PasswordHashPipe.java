/*
   Copyright 2016 Nationale-Nederlanden

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package nl.nn.adapterframework.pipes;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.configuration.ConfigurationWarnings;
import nl.nn.adapterframework.core.IPipeLineSession;
import nl.nn.adapterframework.core.PipeForward;
import nl.nn.adapterframework.core.PipeRunException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.util.PasswordHash;

import org.apache.commons.lang.StringUtils;

/**
 * Hash a password or validate a password against a hash using PasswordHash.java
 * from <a href="https://crackstation.net/hashing-security.htm">https://crackstation.net/hashing-security.htm</a>.
 * Input of the pipe is expected to be the password. In case hashSessionKey
 * isn't used a hash of the password is returned. In case hashSessionKey is used
 * it is validated against the hash in the session key which will determine
 * the forward to be used (success or failure).
 *  
 * <p><b>Configuration:</b>
 * <table border="1">
 * <tr><th>attributes</th><th>description</th><th>default</th></tr>
 * <tr><td>className</td><td>nl.nn.adapterframework.pipes.PasswordHashPipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setName(String) name}</td><td>name of the Pipe</td><td>&nbsp;</td></tr>
 * <tr><td>{@link #setHashSessionKey(String) hashSessionKey}</td>name of sessionKey that holds the hash which will be used to validate the password (input of the pipe)<td></td><td></td></tr>
 * </table>
 * </p>
 * 
 * <b>Exits:</b>
 * <table border="1">
 * <tr><th>state</th><th>condition</th></tr>
 * <tr><td>"success"</td><td>default</td></tr>
 * <tr><td>"failure"</td><td>when hashSessionKey is used and password doesn't validate against the hash</td></tr>
 * </table>
 * 
 * @author Jaco de Groot
 */
public class PasswordHashPipe extends FixedForwardPipe {
	private static String FAILURE_FORWARD_NAME = "failure";
	private String hashSessionKey;

	public void configure() throws ConfigurationException {
		super.configure();
		if (StringUtils.isNotEmpty(getHashSessionKey())) {
			if (findForward(FAILURE_FORWARD_NAME) == null) {
				ConfigurationWarnings configWarnings = ConfigurationWarnings.getInstance();
				configWarnings.add(log, getLogPrefix(null)
						+ "has a hashSessionKey attribute but forward failure is not configured");
			}
		}
	}

	public PipeRunResult doPipe(Object input, IPipeLineSession session)
			throws PipeRunException {
		Object result;
		PipeForward pipeForward;
		if (StringUtils.isEmpty(getHashSessionKey())) {
			try {
				result = PasswordHash.createHash(input.toString());
				pipeForward = getForward();
			} catch (Exception e) {
				throw new PipeRunException(this, "Could not hash password", e);
			}
		} else {
			try {
				result = input;
				if (PasswordHash.validatePassword(input.toString(), (String)session.get(getHashSessionKey()))) {
					pipeForward = getForward();
				} else {
					pipeForward = findForward(FAILURE_FORWARD_NAME);
				}
			} catch (Exception e) {
				throw new PipeRunException(this, "Could not validate password", e);
			}
		}
		return new PipeRunResult(pipeForward, result);
	}

	public String getHashSessionKey() {
		return hashSessionKey;
	}

	public void setHashSessionKey(String hashSessionKey) {
		this.hashSessionKey = hashSessionKey;
	}

}
