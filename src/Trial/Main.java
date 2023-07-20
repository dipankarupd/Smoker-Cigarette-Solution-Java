package Trial;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Main {
    public static void main(String[] args) {
        List<Smoker> smokerList = List.of(
                new Smoker("Trial.Smoker 1", "tobacco", "paper", "matches", new Semaphore(0)),
                new Smoker("Trial.Smoker 2", "paper", "tobacco", "matches", new Semaphore(0)),
                new Smoker("Trial.Smoker 3", "matches", "paper", "tobacco", new Semaphore(0))
        );

        Semaphore agentSemaphore = new Semaphore(1);
        List<String> agentSupplyList = new CopyOnWriteArrayList<>(Arrays.asList("tobacco", "match", "paper"));
        Lock mutex = new ReentrantLock();
        Semaphore smokingSemaphore = new Semaphore(1);

        Thread agentThread = new Thread(() -> {
            Random random = new Random();
            while (true) {
                try {
                    agentSemaphore.acquire();
                    mutex.lock();
                    try {
                        List<String> selectedList = pickRandomItems(agentSupplyList, 2);
                        String ingredient1 = selectedList.get(0);
                        String ingredient2 = selectedList.get(1);

                        System.out.println("Agent places " + ingredient1 + " and " + ingredient2 + " on the table.");

                        Smoker smoker = selectSmoker(smokerList, ingredient1, ingredient2);
                        if (smoker != null) {
                            smoker.getSemaphore().release();
                            System.out.println("Agent wakes up " + smoker.getName() + ".");
                        }
                    } finally {
                        mutex.unlock();
                    }
                    agentSemaphore.release();

                    Thread.sleep(random.nextInt(3000) + 1000); // Wait for a random interval before placing new ingredients
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        agentThread.start();

        for (Smoker smoker : smokerList) {
            Thread smokerThread = new Thread(() -> {
                while (true) {
                    try {
                        smoker.getSemaphore().acquire();
                        System.out.println(smoker.getName() + " takes " + smoker.getIngredientNeeded1() + " and " + smoker.getIngredientNeeded2() + " from the table.");
                        System.out.println(smoker.getName() + " is making a cigarette.");

                        agentSemaphore.acquire();
                        System.out.println(smoker.getName() + " signals the agent.");
                        agentSemaphore.release();

                        smokingSemaphore.acquire();
                        System.out.println(smoker.getName() + " is smoking the cigarette.");
                        Thread.sleep(1000); // Simulating smoking time
                        System.out.println(smoker.getName() + " finishes smoking.");
                        System.out.println("-------------------------------------");
                        smokingSemaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            smokerThread.start();
        }
    }

    public static List<String> pickRandomItems(List<String> items, int count) {
        List<String> selectedItems = new ArrayList<>();
        Random random = new Random();
        int size = items.size();
        for (int i = 0; i < count; i++) {
            int randomIndex = random.nextInt(size);
            String selectedItem = items.get(randomIndex);
            selectedItems.add(selectedItem);
            // Remove the selected item to ensure uniqueness
            items.remove(randomIndex);
            size--;
        }
        items.addAll(selectedItems);
        return selectedItems;
    }

    public static Smoker selectSmoker(List<Smoker> smokerList, String ingredient1, String ingredient2) {
        for (Smoker smoker : smokerList) {
            if (!smoker.getIngredientHave().equals(ingredient1) && !smoker.getIngredientHave().equals(ingredient2)) {
                return smoker;
            }
        }
        return null;
    }
}
