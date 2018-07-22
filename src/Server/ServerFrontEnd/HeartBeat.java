package Server.ServerFrontEnd;

import Conf.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by kamal on 7/28/2017.
 */

public class HeartBeat implements Runnable {
    private Conf.Constants.REPLICAS replicaManagerID;
    private int heartbeatPort;
    private Map<Conf.Constants.REPLICAS, Boolean> replicaManagerStatus;
    private final Object replicaManagerStatusLock = new Object();
    private Map<Conf.Constants.REPLICAS, Long> mostRecentAliveTime;
    private final Object mostRecentAliveTimeLock = new Object();
    private Election election;
    private boolean[] replicasStatus;
    private Conf.Constants.REPLICAS leaderID;

    // A HeartBeat object is created when a RM starts
    public HeartBeat(Conf.Constants.REPLICAS replicaManagerID, int heartbeatPort) throws SocketException {
        this.replicaManagerID = replicaManagerID;
        this.heartbeatPort = heartbeatPort;
        // Start listening to election messages
        this.election = new Election(this.replicaManagerID);

        replicasStatus = new boolean[Conf.Constants.REPLICAS.values().length];
        for (Conf.Constants.REPLICAS replicaID : Conf.Constants.REPLICAS.values())
            replicasStatus[replicaID.getCoefficient() - 1] = true;

        // Assume all RMs are alive when HeartBeat object is initiated
        this.replicaManagerStatus = Collections.synchronizedMap(new HashMap<Conf.Constants.REPLICAS, Boolean>());
        this.replicaManagerStatus.put(Conf.Constants.REPLICAS.KEN_RO, true);
        this.replicaManagerStatus.put(Conf.Constants.REPLICAS.KAMAL, true);
        this.replicaManagerStatus.put(Conf.Constants.REPLICAS.MINH, true);
        this.mostRecentAliveTime = Collections.synchronizedMap(new HashMap<Conf.Constants.REPLICAS, Long>());
        this.mostRecentAliveTime.put(Conf.Constants.REPLICAS.KEN_RO, System.nanoTime() / 1000000);
        this.mostRecentAliveTime.put(Conf.Constants.REPLICAS.KAMAL, System.nanoTime() / 1000000);
        this.mostRecentAliveTime.put(Conf.Constants.REPLICAS.MINH, System.nanoTime() / 1000000);
    }

    @Override
    public void run() {
        // Start listening to HeartBeat messages from other RMs
        new Thread(() -> listenToAliveMessage()).start();

        // Start sending Alive messages to other RMs
        new Thread(() -> broadcastAliveMessage()).start();

        // Start checking Alive status of RMs
        new Thread(() -> checkingAlive()).start();

        // Start a new election after the RM is online
        new Thread(() -> {
//            new Thread(() -> election.listenToElectionMessage()).start();

            try {
                sleep(Constants.ELECTION_DELAY);
//                System.out.println(replicaManagerID.name() + " starts new election");
                election.startElection(replicasStatus);
                election.announceNewLeader();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }).start();
    }

    public void updateLeaderID(Conf.Constants.REPLICAS leaderID) {
        this.leaderID = leaderID;
    }

    private void listenToAliveMessage() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(heartbeatPort);

            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagramPacket);

                new Thread(() -> {
                    long currentTime = System.nanoTime() / 1000000;
                    String dataReceived = new String(datagramPacket.getData()).trim();
                    Conf.Constants.REPLICAS replicaID = Conf.Constants.REPLICAS.valueOf(dataReceived);
//                    System.out.println(replicaManagerID + ": knows " + replicaID + " is alive at " + currentTime);
                    synchronized (replicaManagerStatusLock) {
                        replicaManagerStatus.put(replicaID, true);
                    }
                    synchronized (mostRecentAliveTimeLock) {
                        mostRecentAliveTime.put(replicaID, currentTime);
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private void broadcastAliveMessage() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] buffer = replicaManagerID.name().getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramPacket.setAddress(InetAddress.getLocalHost());
            while (true) {
                for (Conf.Constants.REPLICAS replicaID : Conf.Constants.REPLICAS.values()) {
                    if (replicaID != replicaManagerID) {
                        datagramPacket.setPort(replicaID.getCoefficient() * Constants.PORT_HEART_BEAT);
                        socket.send(datagramPacket);
//                        System.out.println("Send " + replicaManagerID.name() + " is alive to " + replicaID.name());
                    }
                }
                sleep(Constants.HEART_BEAT_DELAY);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    private void checkingAlive() {
        try {
            while (true) {
                for (Conf.Constants.REPLICAS replicaID : Conf.Constants.REPLICAS.values()) {
                    if (isReplicaAlive(replicaID) && replicaID != replicaManagerID) {
                        long mostRecentTime;
                        synchronized (mostRecentAliveTimeLock) {
                            mostRecentTime = mostRecentAliveTime.get(replicaID);
                        }
                        long currentTime = System.nanoTime() / 1000000;
//                            System.out.println("currentTime - mostRecentTime = " + currentTime + " - " + mostRecentTime + " = " + (currentTime - mostRecentTime));

                        // The replica is failed
                        if (currentTime - mostRecentTime > Constants.HEART_BEAT_TIMEOUT) {
                            System.out.println(replicaManagerID.name() + ": knows " + replicaID.name() + " is CRASH, current leader is " + leaderID);
                            synchronized (replicaManagerStatusLock) {
                                replicaManagerStatus.put(replicaID, false);
                            }

                            // Restart the crashed replica
//                            new Thread(() -> {
//                                ReplicaManager replicaManager = new ReplicaManager(replicaID);
//                                new Thread(replicaManager).start();
//                            }).start();

                            // Start new election if leader is failed
                            if (replicaID.equals(leaderID)) {
                                new Thread(() -> {
                                    System.out.println(replicaManagerID + ": starts new election");
                                    election.startElection(replicasStatus);
                                    election.announceNewLeader();
                                }).start();
                            }
                        }
                    }
                }
                sleep(Constants.HEART_BEAT_TIMEOUT);
            }
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    private boolean isReplicaAlive(Conf.Constants.REPLICAS replicaID) {
        synchronized (replicaManagerStatusLock) {
            return replicaManagerStatus.get(replicaID);
        }
    }
}