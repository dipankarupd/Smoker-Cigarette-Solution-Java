package Final;

import java.util.*;
import java.util.concurrent.Semaphore;

class Main {
    public static void main(String[] args) {

        // creating the list of smoker objects:
        List<Smoker> smokerList = List.of(
                new Smoker("Smoker 1", "tobacco"),
                new Smoker("Smoker 2", "paper"),
                new Smoker("Smoker 3", "matches")
        );

        // list of items that agent supplies
        List<String> agentSupplyList = new ArrayList<>(Arrays.asList("tobacco", "matches", "paper"));

        // semaphores for the ingredients:
        Semaphore tobaccoSemaphore = new Semaphore(0);
        Semaphore paperSemaphore = new Semaphore(0);
        Semaphore matchesSemaphore = new Semaphore(0);

        // agent semaphore
        Semaphore agentSemaphore = new Semaphore(1);

        // semaphore that deals with smoking phase
        Semaphore smokingSemaphore = new Semaphore(1);

        // agent thread
        Thread agentThread = new Thread(() -> {
            Random random = new Random();
            while (true) {
                try {

                    // wait on agent sea=maphore
                    agentSemaphore.acquire();

                    // agent picks two random items
                    List<String> selectedList = pickRandomItems(agentSupplyList, 2);
                    String ingredient1 = selectedList.get(0);
                    String ingredient2 = selectedList.get(1);

                    System.out.println("Agent places " + ingredient1 + " and " + ingredient2 + " on the table.");

                    // agent releases the ingredient semaphores based on the items picked
                    if ((ingredient1.equals("tobacco") && ingredient2.equals("matches")) ||
                            (ingredient1.equals("matches") && ingredient2.equals("tobacco"))) {
                        paperSemaphore.release();
                    } else if ((ingredient1.equals("tobacco") && ingredient2.equals("paper")) ||
                            (ingredient1.equals("paper") && ingredient2.equals("tobacco"))) {
                        matchesSemaphore.release();
                    } else if ((ingredient1.equals("matches") && ingredient2.equals("paper")) ||
                            (ingredient1.equals("paper") && ingredient2.equals("matches"))) {
                        tobaccoSemaphore.release();
                    }

                    // signal on agent semaphore
                    agentSemaphore.release();

                    Thread.sleep(random.nextInt(3000) + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // begin the agent thread
        agentThread.start();

        for (Smoker smoker : smokerList) {

            // create smoker thread, one for each smokers
            Thread smokerThread = new Thread(() -> {
                while (true) {
                    try {

                        // get the ingredient that smoker has
                        String smokerIngredient = smoker.getIngredientHave();

                        // wait on the ingredient of the smoker
                        if (smokerIngredient.equals("tobacco")) {
                            tobaccoSemaphore.acquire();
                        } else if (smokerIngredient.equals("paper")) {
                            paperSemaphore.acquire();
                        } else if (smokerIngredient.equals("matches")) {
                            matchesSemaphore.acquire();
                        }

                        System.out.println(smoker.getName() + " takes the required ingredients from the table.");
                        System.out.println(smoker.getName() + " is making a cigarette.");

                        agentSemaphore.acquire();
                        System.out.println(smoker.getName() + " signals the agent.");
                        agentSemaphore.release();

                        // wait on the smoking semaphore...indicate a smoker is smoking
                        smokingSemaphore.acquire();
                        System.out.println(smoker.getName() + " is smoking the cigarette.");
                        Thread.sleep(1000); // Simulating smoking time
                        System.out.println(smoker.getName() + " finishes smoking.");
                        System.out.println("-------------------------------------");

                        // release the smoking semaphore
                        smokingSemaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            smokerThread.start();
        }
    }


    // utility method to pick the random items from the items of the agent
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
}