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

package magma.tools.benchmark.model.bench.rollingballchallenge;

import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

/**
 *
 * @author kdorer
 */
public class RollingBallBenchmarkTeamResult extends TeamResult
{
	public RollingBallBenchmarkTeamResult(String name)
	{
		super(name);
	}

	@Override
	public float getAverageScore()
	{
		return getAverageDistance();
	}

	public float getAverageDistance()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			// NOT NICE: stopped with compositum pattern half way through
			if (result instanceof RollingBallBenchmarkTeamResult) {
				avg += ((RollingBallBenchmarkTeamResult) result).getAverageDistance();
			} else {
				avg += ((RollingBallBenchmarkSingleResult) result).getDistance();
			}
		}
		return avg / results.size();
	}

	public float getLastDistance()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		ISingleResult result = results.get(results.size() - 1);
		if (result instanceof RollingBallBenchmarkTeamResult) {
			return ((RollingBallBenchmarkTeamResult) result).getLastDistance();
		} else {
			return ((RollingBallBenchmarkSingleResult) result).getDistance();
		}
	}
}
