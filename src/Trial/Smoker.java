package Trial;

import java.util.concurrent.Semaphore;

class Smoker {
    private String name;
    private String ingredientHave;
    private String ingredientNeeded1;
    private String ingredientNeeded2;
    private Semaphore ingredientHavesemaphore;

    public Smoker(String name, String ingredientHave, String ingredientNeeded1, String ingredientNeeded2, Semaphore semaphore) {
        this.name = name;
        this.ingredientHave = ingredientHave;
        this.ingredientNeeded1 = ingredientNeeded1;
        this.ingredientNeeded2 = ingredientNeeded2;
        this.ingredientHavesemaphore = semaphore;
    }
    public String getName() {
        return name;
    }
    public String getIngredientHave() {
        return ingredientHave;
    }
    public String getIngredientNeeded1() {
        return ingredientNeeded1;
    }
    public String getIngredientNeeded2() {
        return ingredientNeeded2;
    }
    public Semaphore getSemaphore() {
        return ingredientHavesemaphore;
    }
}
