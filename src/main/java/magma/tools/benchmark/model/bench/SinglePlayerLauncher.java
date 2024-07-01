/* Copyright 2009 Hochschule Offenburg
 * Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber,
 * Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
 * Joachim Schilling, Rajit Shahi
 *
 * This file is part of magmaOffenburg.
 *
 * magmaOffenburg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * magmaOffenburg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with magmaOffenburg. If not, see <http://www.gnu.org/licenses/>.
 */

package magma.tools.benchmark.model.bench;

import hso.autonomy.util.file.StreamBufferer;
import hso.autonomy.util.misc.CommandUtil;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SinglePlayerLauncher
{
	/** number of cycles to wait before next player starts */
	private static int WAIT_CYCLES_LAUNCHING;

	private final String serverIP;

	private final int agentPort;

	private final String path;

	private final String binary;

	private final String challengeName;

	private final boolean isGazebo;

	private int cyclesToWait;

	private boolean started;

	private StreamBufferer stdOut;

	private StreamBufferer stdErr;

	public SinglePlayerLauncher(
			String serverIP, int agentPort, String path, String binary, String challengeName, boolean isGazebo)
	{
		this.serverIP = serverIP;
		this.agentPort = agentPort;
		this.path = path;
		this.binary = binary;
		this.challengeName = challengeName;
		this.isGazebo = isGazebo;
		started = false;
		WAIT_CYCLES_LAUNCHING = isGazebo ? 100 : 10;
		cyclesToWait = WAIT_CYCLES_LAUNCHING;
	}

	/**
	 * Launches the player
	 * @return false if no more players have to be launched
	 */
	public boolean launchPlayer(RunInformation runInfo, int playersOnField)
	{
		if (playersOnField >= 1 || isGazebo) {
			cyclesToWait--;
			if (cyclesToWait < 0) {
				cyclesToWait = WAIT_CYCLES_LAUNCHING;
				started = false;
				return false;
			}
		}
		if (!started) {
			startPlayer(runInfo);
			started = true;
		}
		return true;
	}

	private void startPlayer(RunInformation runInfo)
	{
		runScript(binary, new Object[] {serverIP, agentPort, runInfo.getBeamX(), runInfo.getBallY(), challengeName});
	}

	public void stopPlayer()
	{
		Object[] arguments = {""};
		runScript("kill.sh", arguments);
	}

	private void runScript(String scriptName, Object[] arguments)
	{
		String expandedPath = path.replaceFirst("^~", System.getProperty("user.home"));
		File workingDir = new File(expandedPath);
		File fullPath = new File(workingDir, scriptName);
		if (!validatePath(fullPath)) {
			return;
		}

		String command =
				Stream.concat(Stream.of("bash", fullPath.getPath()), Arrays.stream(arguments).map(Object::toString))
						.collect(Collectors.joining(" "));
		System.out.println(command);

		Process ps = CommandUtil.launch(command, null, workingDir);
		stdOut = new StreamBufferer(ps.getInputStream(), 5000);
		stdErr = new StreamBufferer(ps.getErrorStream(), 5000);
	}

	public String getStatusText()
	{
		if (stdOut == null || stdErr == null) {
			return "";
		}
		return "stderr: " + stdErr.getBuffer() + "\nstdout: " + stdOut.getBuffer();
	}

	private boolean validatePath(File path)
	{
		if (path.exists()) {
			return true;
		}
		System.out.println("Path " + path.getAbsolutePath() + " does not exist.");
		return false;
	}
}