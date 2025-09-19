package logic.interception;

/**
 * 🚀 LaunchWindowCalculator - Calculates optimal launch windows for drone interceptors
 * to ensure successful engagement of moving threats.
 */
public class LaunchWindowCalculator {

    /**
     * Determines if launching now will result in interception.
     * @param interceptorSpeed speed of interceptor in m/s
     * @param targetSpeed speed of target in m/s
     * @param distance initial distance between them
     * @param maxTimeToIntercept maximum allowed time (s)
     * @return true if launch is viable
     */
    public boolean canIntercept(double interceptorSpeed, double targetSpeed, double distance, double maxTimeToIntercept) {
        if (interceptorSpeed <= targetSpeed || interceptorSpeed <= 0) return false;

        double relativeSpeed = interceptorSpeed - targetSpeed;
        double timeToIntercept = distance / relativeSpeed;

        return timeToIntercept <= maxTimeToIntercept;
    }

    /**
     * Computes time required for successful intercept.
     * @param interceptorSpeed interceptor speed
     * @param targetSpeed target speed
     * @param distance initial separation
     * @return time in seconds or -1 if not possible
     */
    public double computeTimeToIntercept(double interceptorSpeed, double targetSpeed, double distance) {
        if (interceptorSpeed <= targetSpeed || interceptorSpeed <= 0) return -1;

        double relativeSpeed = interceptorSpeed - targetSpeed;
        return distance / relativeSpeed;
    }

    /**
     * Determines latest time to launch (given delay).
     */
    public double latestLaunchTime(double interceptorSpeed, double targetSpeed, double distance, double delaySec) {
        double timeToIntercept = computeTimeToIntercept(interceptorSpeed, targetSpeed, distance);
        return timeToIntercept - delaySec;
    }
}
