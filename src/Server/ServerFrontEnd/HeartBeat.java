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



public class HeartBeat implements Runnable {
    private Conf.Constants.REPLICAS RM_ID; /* Replica Manager ID */
    private int heartbeatPort;
    private Map<Conf.Constants.REPLICAS, Boolean> RM_Status;
    private final Object RM_StatusLock = new Object();
    private Map<Conf.Constants.REPLICAS, Long> LastAliveTime;
    private final Object LastAlive = new Object();
    private Election election;
    private boolean[] replicasStatus;
    private Conf.Constants.REPLICAS leaderID;

    // A HeartBeat object is created when a RM starts
    public HeartBeat(Conf.Constants.REPLICAS RM_ID, int heartbeatPort) throws SocketException {
        this.RM_ID = RM_ID;
        this.heartbeatPort = heartbeatPort;
        // Start listening to election messages
        this.election = new Election(this.RM_ID);

        replicasStatus = new boolean[Conf.Constants.REPLICAS.values().length];
        for (Conf.Constants.REPLICAS replicaID : Conf.Constants.REPLICAS.values())
            replicasStatus[replicaID.getCoefficient() - 1] = true;

        // Assume all RMs are alive when HeartBeat object is initiated
        this.RM_Status = Collections.synchronizedMap(new HashMap<Conf.Constants.REPLICAS, Boolean>());
        this.RM_Status.put(Conf.Constants.REPLICAS.Replica2, true);
        this.RM_Status.put(Conf.Constants.REPLICAS.Replica1, true);
        this.RM_Status.put(Conf.Constants.REPLICAS.Replica3, true);
        this.LastAliveTime = Collections.synchronizedMap(new HashMap<Conf.Constants.REPLICAS, Long>());
        this.LastAliveTime.put(Conf.Constants.REPLICAS.Replica2, System.nanoTime() / 1000000);
        this.LastAliveTime.put(Conf.Constants.REPLICAS.Replica1, System.nanoTime() / 1000000);
        this.LastAliveTime.put(Conf.Constants.REPLICAS.Replica3, System.nanoTime() / 1000000);
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
//                System.out.println(RM_ID.name() + " starts new election");
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
//                    System.out.println(RM_ID + ": knows " + replicaID + " is alive at " + currentTime);
                    synchronized (RM_StatusLock) {
                        RM_Status.put(replicaID, true);
                    }
                    synchronized (LastAlive) {
                        LastAliveTime.put(replicaID, currentTime);
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
            byte[] buffer = RM_ID.name().getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            datagramPacket.setAddress(InetAddress.getLocalHost());
            while (true) {
                for (Conf.Constants.REPLICAS replicaID : Conf.Constants.REPLICAS.values()) {
                    if (replicaID != RM_ID) {
                        datagramPacket.setPort(replicaID.getCoefficient() * Constants.PORT_HEART_BEAT);
                        socket.send(datagramPacket);
//                        System.out.println("Send " + RM_ID.name() + " is alive to " + replicaID.name());
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
                    if (isReplicaAlive(replicaID) && replicaID != RM_ID) {
                        long mostRecentTime;
                        synchronized (LastAlive) {
                            mostRecentTime = LastAliveTime.get(replicaID);
                        }
                        long currentTime = System.nanoTime() / 1000000;
//                            System.out.println("currentTime - mostRecentTime = " + currentTime + " - " + mostRecentTime + " = " + (currentTime - mostRecentTime));

                        // The replica is failed
                        if (currentTime - mostRecentTime > Constants.HEART_BEAT_TIMEOUT) {
                            System.out.println(RM_ID.name() + ": knows " + replicaID.name() + " is CRASH, current leader is " + leaderID);
                            synchronized (RM_StatusLock) {
                                RM_Status.put(replicaID, false);
                            }

                            // Restart the crashed replica
//                            new Thread(() -> {
//                                ReplicaManager replicaManager = new ReplicaManager(replicaID);
//                                new Thread(replicaManager).start();
//                            }).start();

                            // Start new election if leader is failed
                            if (replicaID.equals(leaderID)) {
                                new Thread(() -> {
                                    System.out.println(RM_ID + ": starts new election");
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
        synchronized (RM_StatusLock) {
            return RM_Status.get(replicaID);
        }
    }
}