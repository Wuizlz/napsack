import java.util.*;

/**
 * CS33200 Algorithms - Knapsack Project
 *
 * Implements:
 * 1) 0/1 Knapsack using Dynamic Programming (optimal)
 * 2) Fractional Knapsack using Greedy by value/weight ratio (optimal)
 *
 * Input format (from standard input):
 *
 * n W
 * value1 weight1
 * value2 weight2
 * ...
 * valueN weightN
 *
 * Example:
 * 3 50
 * 60 10
 * 100 20
 * 120 30
 */

public class KnapsackProject {
    // Represents an item with value, weight, and its original index
    static class Item {
        int value;
        int weight;
        int index; // 1-based index

        Item(int value, int weight, int index) {
            this.value = value;
            this.weight = weight;
            this.index = index;
        }
    }

    static class FractionChoice {
        Item item;
        double fraction;

        FractionChoice(Item item, double fraction) {
            this.item = item;
            this.fraction = fraction;
        }
    }

    /**
     * 0/1 Knapsack using Dynamic Programming.
     *
     * dp[i][w] = best value using first i items with capacity w.
     *
     * @param capacity the knapsack capacity W
     * @param items    array of items
     * @param chosen   output array: chosen[i] = true if items[i] is taken
     * @return maximum total value achievable
     */

    public static int knapSack01(int capacity, Item[] items, boolean[] chosen) {
        int n = items.length;
        int[][] dp = new int[n + 1][capacity + 1];
        // Build DP table iteratively
        for (int i = 1; i <= n; i++) {
            int wt = items[i - 1].weight;
            int val = items[i - 1].value;
            for (int w = 0; w <= capacity; w++) {
                if (wt > w) {
                    // Item too heavy: cannot take it
                    dp[i][w] = dp[i - 1][w];

                } else {
                    // Either skip or take this itme
                    dp[i][w] = Math.max(dp[i - 1][w], // skip item i
                            val + dp[i - 1][w - wt]); // take item i
                }
            }
        }
        // Reconstruct chosen items by walking backwards through dp table
        Arrays.fill(chosen, false);
        int w = capacity;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                // Item i-1 was included
                chosen[i - 1] = true;
                w -= items[i - 1].weight;
            }
        }
        return dp[n][capacity];
    }

      /**
     * Fractional Knapsack using greedy algorithm by value/weight ratio.
     *
     * @param capacity knapsack capacity (double, to allow fractions)
     * @param items    array of items
     * @param choices  output: list of FractionChoice showing how much of each item is taken
     * @return maximum total value achievable
     */

      public static double fractionalKnapsack(double capacity, Item[] items, List<FractionChoice> choices)
      {
        int n = items.length;

        Item[] sorted = Arrays.copyOf(items, n);
        Arrays.sort(sorted, new Comparator<Item>() {
            @Override public int compare(Item a , Item b)
            {
                double r1 = (double) a.value / a.weight;
                double r2 = (double) b.value / b.weight;
                return Double.compare(r2, r1);
            }
        });
        double remaining = capacity;
        double totalValue = 0.0;
        choices.clear();

        //Take whole items or fractions until the knapsack is full
        for (int i = 0; i< n && remaining > 0.0; i++)
        {
            Item it = sorted[i];
            if (it.weight <= remaining){
                //Take the whole item 
                remaining -= it.weight;
                totalValue += it.value;
                choices.add(new FractionChoice(it, 1.0));
            }
            else{
                //Take only a fraction of this item 
                double fraction = remaining / it.weight;
                totalValue += it.value * fraction;
                choices.add(new FractionChoice(it, fraction));
                remaining = 0.0; // knapsack is now full
            }
        }
        return totalValue;
      }

      public static void main (String[] args)
      {
        Scanner sc = new Scanner(System.in);

        //Read input 
        System.out.print("Enter number of items (n)");
        int n = sc.nextInt();

        System.out.print("Enter knapsack capacity (W): ");
        int W = sc.nextInt();

        Item[] items = new Item[n];
        System.out.println("Enter value and weight for each item ");
        for (int i = 0; i < n; i++)
        {
            int value = sc.nextInt();
            int weight = sc.nextInt();
            items[i] = new Item(value, weight, i+1); //index is 1-based
        }

        //Knapsack Dp

        boolean[] chosen01 = new boolean[n];
        int optimal01 = knapSack01(W, items, chosen01);
        System.out.println();
        System.out.println("=== 0/1 Knapsack (DP) ===");
        System.out.println("Optimal total value = " + optimal01);
        System.out.println("Items taken (index, value, weight):");
        for(int i =0; i<n; i++)
        {
            if(chosen01[i])
            {
                System.out.printf("    Item %d: value=%d, weight=%d%n",
                    items[i].index, items[i].value, items[i].weight);
    
            }
        }
        //Fractional Knapsack greedy
        List<FractionChoice> fracChoices = new ArrayList<>();
        double optimalFrac = fractionalKnapsack((double) W, items, fracChoices);

        System.out.println();
        System.out.println("=== Fractional Knapsack (Greedy but value/weight) ===");
        System.out.printf("Optimal total value = %.2f%n", optimalFrac);
         System.out.println("Items taken (index, value, weight, fraction taken):");
        for (FractionChoice fc : fracChoices) {
            System.out.printf("  Item %d: value=%d, weight=%d, fraction=%.4f%n",
                    fc.item.index, fc.item.value, fc.item.weight, fc.fraction);
        }

        sc.close();

      }


}