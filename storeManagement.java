/*
 * @Author: Eirene Mehta
 * Description: Creates a store, with shopping carts, customer info, and a product 
 * catalogue using three array lists. Allows multiple customers to create multiple carts. 
 * caclulates checkout costs, inventory management, customer management, and does not allow
 * customers to buy items which are out of stock. Purchases are confirmed twice (at order desk)
 * and at checkout. 
 */
// package WorkingWithFiles;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.File;

class storeManagement {
    // Global Variable Declarations
    private static final String customerFilePath = "./customerList.txt";
    private static final String catalogueFilePath = "./catalogueList.txt";
    private static Scanner uI = new Scanner (System.in);
    private static boolean deskCompleted = false; 

    // Array lists
    private static ArrayList <catalogue> catalogues = new ArrayList<catalogue>(); //holds catalogue list
    private static ArrayList <customer> customers = new ArrayList<customer>(); //holds customers list
    private static ArrayList <cart> carts = new ArrayList <cart>(); //holds shopping carts
    public static void main (String[] args) {
        readCatalogue(); // read files
        readCustomers();
        boolean again = true;
        System.out.println("Welcome!");
        while (again == true) {  // loops menu until save and exit is chosen
            try {
                System.out.println("-------- Main Menu --------");
                System.out.println("1. Inventory Management");
                System.out.println("2. Customer Management");
                System.out.println("3. Point of Sale");
                System.out.println("4. Save all information and exit");

                int userChoice = assureInt(1,4); //assures user enters choice within range

                switch (userChoice) { //switch for user choice 
                    case 1:
                    inventoryManagement(); //opens another menu
                    break;

                    case 2:
                    // display customers before hand
                    System.out.println("-------- Customer Management --------");
                    System.out.println("1. Add customer");
                    System.out.println("2. Remove customer");
                    System.out.println("3. Display customers");
                    System.out.println("4. Exit Customer Management Menu");
                    userChoice = assureInt(1, 4);

                    switch (userChoice) { //nested switch
                        case 1:
                        addCustomer();
                        break;
                        case 2:
                        removeCustomer();
                        break;
                        case 3:
                        displayCustomers();
                        break;
                        case 4:
                        break;
                    }
                    break;

                    case 3:
                    pointOfSale(); //point of sale menu
                    break;

                    case 4:
                    again = false; //breaks while loop for menu
                    saveCustomers(); //save array lists to file
                    saveCatalogue();
                    System.out.println("Goodbye!");
                    // displays order 
                    break;
                }

            } catch (InputMismatchException e) { //catches non-integer input
                System.out.println("Invalid selection");
                uI.nextLine(); //asks for input again
            } // end of try-catch

        } // end of while loop

    } // end of main method

    public static void inventoryManagement() { //Inventory management menu
        System.out.println("-------- Inventory Management --------");
        System.out.println("1. Add items to inventory");
        System.out.println("2. Add new products to catalogue");
        System.out.println("3. Remove items as they are sold");
        System.out.println("4. Remove items from catalogue");
        System.out.println("5. Edit numbers in stock for an item");
        System.out.println("6. Display Catalogue");
        System.out.println("7. Exit Inventory Menu");
        int userChoice = assureInt(1, 7);
        switch(userChoice) {
            case 1:
            addStock();
            break;
            case 2:
            addProduct();
            break;
            case 3:
            removeStock();
            break;
            case 4:
            removeProduct();
            break;
            case 5:
            editStock();
            break;
            case 6:
            displayCatalogue();
            case 7:
            break;
        }
    }

    public static void pointOfSale() {
        boolean orderCompleted = false;
        
        while (orderCompleted == false) {
        System.out.println("-------- Point of Sale --------");
        System.out.println("1. Order Desk");
        if (deskCompleted == false) {
            System.out.println("2. Order pick display [proceed to order desk first]");
        } else {
            System.out.println("2. Order pick display");
        }
        if (deskCompleted == false) {
            System.out.println("3. Check out [please proceed to order desk first]");
        } else {
            System.out.println("3. Check out");
        }
        System.out.println("4. Exit Point of Sale Menu");
        int userChoice = assureInt(1, 4);
        switch(userChoice) {
            case 1:
            orderDesk();
            break;
            case 2:
            orderPickDisplay();
            break;
            case 3:
            checkOut(); 
            break;
            case 4:
            orderCompleted=true;
            break;
        }
    }

    } // end of main method

    public static void orderDesk() {
        System.out.println("You are in the order desk. Give me the customer's info.");

        System.out.println("Phone Number:"); //save each entry to local variables
        String cusID = uI.next();
        System.out.println("First Name:");
        String cusFirst = uI.next();
        System.out.println("Last Name:");
        String cusLast = uI.next();

        boolean foundCus = false; //booolean if customer is found
        for (customer c : customers) {
            if (c.getCustomerID().equalsIgnoreCase(cusID) && c.getFirstName().equalsIgnoreCase(cusFirst) && c.getLastName().equalsIgnoreCase(cusLast));
            foundCus = true; //set to true when found
            break;
        }

        if (foundCus == false) { //asks if customer should be added if not found
            System.out.println("That customer doesn't exist. Add customer?\n1. Yes\n2. No\n");
            if (assureInt(1, 2) == 1) { //if one is entered
                customer addedCus = new customer (cusID, cusFirst, cusLast); //add the customer
                customers.add(addedCus); //adds to array list
                System.out.println("Customer added sucessfully.");
            } else {
                System.out.println("Returning back to menu."); //if no, nothing else to do asides from return to menu
                return;
            }
        }

        System.out.println("Please choose what the customer has bought from the catalogue.");
        boolean shopping = true; // boolean for creating the shopping loop
        while (shopping) { //loops to add more items if desired
            displayCatalogue();
            System.out.println("Enter the index of the item which you would like to add");
            int bought = assureInt(1, catalogues.size()) - 1; //minus one for array indexing
            catalogue chosenCatalogue = catalogues.get(bought); //sets chosenCat to the cat selected

            if (Integer.valueOf(chosenCatalogue.getStockQty()) == 0) {
                System.out.println("There's no more left in stock"); //message when none left in stock
                continue; //continues for code to loop again
            }

            System.out.println("How many did the customer buy?");
            System.out.printf("psst cannot exceed %s\n", chosenCatalogue.getStockQty()); //customer cannot buy morre items than in stock
            int numBought = assureInt(1, Integer.valueOf(chosenCatalogue.getStockQty())); // assures that user cannot enter more than in stock

            System.out.printf("Confirm purchase: %d of %s\n1. Yes\n2. No\n", numBought, chosenCatalogue.getDescription());
            int ans = assureInt(1, 2); //assures user chooses yes or no

            if (ans ==1) { //if confirmed, the item is added to cart
                cart currBuy = new cart (chosenCatalogue, numBought, cusID); 
                carts.add(currBuy); //add curremt item bought
                chosenCatalogue.removeStock(numBought);
                System.out.println("Item sucessfully added to cart.");
            } else {
                System.out.println("Order cancelled."); // item not added to cart otherwise
            }

            System.out.println("Did the customer buy any other itens?\n1. Yes\n2. No");
            if (assureInt(1,2) == 1) {
                shopping = true; //keeps in loop if customer bought more items
            } else {
                shopping = false; //else breaks loop
            }
            
        }
    }

    public static void orderPickDisplay() {
        boolean existence = false; //boolean for if customer exists
        System.out.println("Enter customer ID to view the customer's cart: ");
        String cusID = uI.next(); //holds customer id

        for(int i = 0; i < carts.size(); i++) { //loops to find customer
            cart curCart = carts.get(i); //gets the customer's cart
            if (cusID.equals(curCart.getCustomerID())) {
                existence = true; //when found the bolean is set to true
                System.out.println("------ Custoomer Cart ------");
                catalogue thisCat = curCart.getCatalogue();
                System.out.printf("%s: Items in cart: %d, Items in Stock: %s, Price per item: %s\n", thisCat.getDescription(), curCart.getNumBought(), thisCat.getStockQty(), thisCat.getPrice());
            }
        }
        if (existence == false) { //if no cart is found, displays message
            System.out.println("Cart does not exist for that customer ID.");
        }
    }

    public static void checkOut() {
        boolean cashing = true;
        boolean cusFound = false; //boolean to hold if customer is found
        if (!(carts.isEmpty())) { //only runs when there are existing carts
            System.out.println("Enter the customer ID for customer which is checking out.");
            String cusID2 = uI.next(); //holds customer id
            double total = 0;
            // System.out.println(cusID);
            for (customer c : customers) { //loop to go through customers
                if (c.getCustomerID().equals(cusID2)) { //finds if customer exists
                    cusFound = true;

                    for (cart curCart : carts) { //loops through existing carts for customer
                        catalogue cat = curCart.getCatalogue(); //item bought
                        int quantity = curCart.getNumBought(); //num bought by customer
                        double itemTotal = quantity * Integer.valueOf(cat.getPrice()); //single item total
                        total = total + itemTotal; //sums up total
                        System.out.printf("%s, Items in cart: %d, Price: %.2f\n", cat.getDescription(), quantity, itemTotal);
                    }

                System.out.printf("Your total is %.2f\n", total); //displays added cost
                System.out.println("Confirm?\n1. Yes\n2. No");
                int confirm = assureInt(1,2); //confirm answer yes/no
                if (confirm ==1) {
                    System.out.println("Thank you for your purchase!");
                    saveCatalogue();
                } else {      //restores stock when order confirmation is 'no'
                    for (int i = 0; i < carts.size(); i++) {
                        cart currentCart = carts.get(i);
                        if (cusID2.equals(currentCart.getCustomerID())){
                            catalogue cat = currentCart.getCatalogue();
                            int quantity = currentCart.getNumBought();
                            cat.addStock(quantity); // restores stock qty
                            carts.remove(i); //removes cart from array list
                        }
                    }
                    System.out.println("Cart(s) removed and stock quantities restored."); //display message
                    return; //return back to menu
                }
                }
            }
            if (cusFound == false) { //if customer is not found
                System.out.println("Sorry, no customer with that ID found.");
                return;
            }
        }
    }



    public static void readCustomers() { //reads file
        try{
            File customerList = new File (customerFilePath);
            Scanner fileScanner= new Scanner (customerList);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] linedata = line.split(","); //splits comma seperated values
                customer currentCustomer = new customer (linedata[0], linedata[1], linedata[2]);
                customers.add(currentCustomer);
            }
        fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error. File not found.");
        }
    } // end of readCustomers()

    public static void readCatalogue() { //reads catalogue file
        try{
        File catalogueList = new File (catalogueFilePath);
        Scanner catalogueScanner = new Scanner (catalogueList);
        while (catalogueScanner.hasNextLine()) {
            String line = catalogueScanner.nextLine();
            String[] linedata = line.split(",");
            catalogue currentCatalogue = new catalogue (linedata[0], linedata[1], linedata[2], linedata[3]);
            catalogues.add(currentCatalogue);

        }
        catalogueScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error, file not found.");
        }
    }

    public static void displayCustomers() { //loops customers array to display customers
        if (customers.isEmpty()) {
            System.out.println("The customers list is currently empty.");
        } else {
            System.out.println("-------- Customer List --------");
            for (int i = 0; i < customers.size(); i++) {
                customer u = customers.get(i);
                System.out.printf("[" + (i + 1) + "] " + "Customer ID:" + u.getCustomerID() + ", " + "Name: " + u.getFirstName()+ " " + u.getLastName() + "\n");
            }
        }

    }
    public static void displayCatalogue() {
        if (catalogues.isEmpty()) {
            System.out.println("The catalogue is currently empty.");
        } else {
            System.out.println("-------- Catalogue List --------");
            for (int i = 0; i < catalogues.size(); i++) {
                catalogue c = catalogues.get(i);
                System.out.printf("[" + (i + 1) + "] " + "Product Number: " + c.getProductNumber() + ", " + "Price: " + c.getPrice() + ", " + "Stock Quantity: " + c.getStockQty() + ", " + "Description: " + c.getDescription() + "\n");
            }
        }
        
    }
    public static int assureInt(int min, int max) { //assures input
        int input;
        while (true) {
            if (uI.hasNextInt()) {
                input = uI.nextInt();
                if (input >= min && input <= max) {
                    return input;
                }
            } else {
                System.out.print("Invalid input. Enter a number: ");
                uI.next();
            }
        }
    }

    public static void removeCustomer() { //removes customers
        displayCustomers();
        System.out.println("Please enter an index to remove");
        try {
            int index = uI.nextInt() - 1;
            uI.nextLine();
            customers.remove(index);
            System.out.println("Customer sucessfully removed.");
        } catch (Exception e) {
            System.out.println("Error."); //error message
        }
    }

    public static void removeProduct() { //resmoves product
        displayCatalogue();
        System.out.println("Please enter an index to remove");
        try {
            int index = uI.nextInt() -1;
            uI.nextLine();
            catalogues.remove(index);
            System.out.println("Product sucessfully removed.");
        } catch (Exception e) {
            System.out.println("Error.");
        }
    }

    public static void editStock() {
        displayCatalogue();
        System.out.println("Please enter the index of the item whose stock quantity you would like to edit.");
        int index = (assureInt(1, catalogues.size())) - 1;
        System.out.println("What is the new stock quantity?");
        String newQty = Integer.toString(assureInt(1, 1000000));
        catalogue temp = catalogues.get(index);
        temp.setStockQty(newQty);
        System.out.printf("Stock quantity sucessfully updated to %s\n", temp.getStockQty());
    }

    public static void addStock() {
        System.out.println("Please enter the index of the item whose stock you would like to add to.");
        displayCatalogue();
        int index = (assureInt(1, catalogues.size())) - 1;
        System.out.println("How much would you like to add to the stock quantity?");
        int added = assureInt(1, 10000000);
        catalogue tempAdded = catalogues.get(index);
        tempAdded.addStock(added);
        System.out.printf("Stock quantity sucessfully changed to %s\n", tempAdded.getStockQty());
    }

    public static void removeStock() {
        displayCatalogue();
        System.out.println("Please enter the idex of the item whose stock you would like to remove from.");
        int index = assureInt(1, catalogues.size()) - 1;
        System.out.println("How many items were sold from the stock quantity?");
        int minused = assureInt(1, 1000000);
        catalogue tempRemoved = catalogues.get(index);
        while (Integer.parseInt(tempRemoved.getStockQty()) - minused < 0) {
            System.out.printf("Cannot remove more items than there are in stock. Please enter a number equal to or less than %s\n", tempRemoved.getStockQty());
            minused = assureInt(1, 100000000);
        }
        tempRemoved.removeStock(minused);
        System.out.printf("Stock quantity sucessfully changed to %s\n", tempRemoved.getStockQty());
    }

    public static void addCustomer() {
        uI.nextLine();
        String tempHolder[] = new String [3];
        System.out.println("Please enter a phone number that will be used as Customer ID.");
        tempHolder[0] = uI.nextLine();
        System.out.println("Please enter the customer's first name: ");
        tempHolder[1] = uI.nextLine();
        System.out.println("Please enter the customer's last name: ");
        tempHolder[2] = uI.nextLine();
        customer currentCustomer = new customer (tempHolder[0], tempHolder[1], tempHolder[2]);
        customers.add(currentCustomer);
        System.out.println("Contact added successfully.");
    }

    public static void addProduct() {
        uI.nextLine();
        String tempProduct[] = new String [4];
        System.out.println("Please enter a six digit product number for inventory organization: ");
        tempProduct[0] = Integer.toString(assureInt(1, 1000000000));
        while (!(tempProduct[0].length() == 6)) {
            System.out.println("Product number must be six digits.");
            tempProduct[0] = Integer.toString(assureInt(1, 1000000000));
        }
        System.out.println("Please enter a price: ");
        tempProduct[1] = Integer.toString(assureInt(1, 10000000));
        System.out.println("Please enter the stock quantity: ");
        tempProduct[2] = Integer.toString(assureInt(1, 10000000));
        System.out.println("Please enter a product description: ");
        uI.nextLine();
        tempProduct[3] = uI.nextLine();
        catalogue currentCatalogue = new catalogue (tempProduct[0], tempProduct[1], tempProduct[2], tempProduct[3]);
        catalogues.add(currentCatalogue);
        System.out.println("Catalogue product added sucessfully.");
    }

    public static void saveCustomers() {
        try {
            PrintWriter diskWriter2 = new PrintWriter (customerFilePath);
            for (int i = 0; i < customers.size(); i++) {
                customer currCustomer = customers.get(i);
                diskWriter2.println(currCustomer.getCustomerID() + "," + currCustomer.getFirstName() + "," + currCustomer.getLastName());
            }
            diskWriter2.close();
        } catch (IOException e) {
            System.out.println("Error saving customers.");
        }
    }

    public static void saveCatalogue() {
        try {
            PrintWriter diskWriter = new PrintWriter (catalogueFilePath);
            for (int i = 0; i < catalogues.size(); i++) {
                catalogue currCat = catalogues.get(i);
                diskWriter.println(currCat.getProductNumber() + "," + currCat.getPrice() + "," + currCat.getStockQty() + "," + currCat.getDescription());
            }
            diskWriter.close();
        } catch (IOException e) {
            System.out.println("Error saving catalogue");
        }
    }

} // end of class

    class customer {
        public String customerID;
        public String firstName;
        public String lastName;

        public customer (String c, String f, String l) {
            customerID = c;
            firstName = f;
            lastName = l;
        }

        // Getters
        public String getCustomerID() {
            return customerID;
        }
        public String getFirstName() {
            return firstName;
        }
        public String getLastName() {
            return lastName;
        }
    } // end of customer class

    class catalogue {
        public String productNumber;
        public String price;
        public String stockQty;
        public String description;

        public catalogue (String p, String r, String s, String d) {
            productNumber = p;
            price = r;
            stockQty = s;
            description = d;
        }

        // Getters
        public String getProductNumber() {
            return productNumber;
        }
        public String getPrice() {
            return price;
        }
        public String getStockQty() {
            return stockQty;
        }
        public String getDescription() {
            return description;
        }
        // Setters
        public void setStockQty(String currentStock) {
            this.stockQty = currentStock;

        }
        public void addStock(int addedQty) {
            int currentStock = Integer.valueOf(this.stockQty); // sets currentstock and casts to int
            this.stockQty = Integer.toString(currentStock + addedQty); // add then cast back to string
        }
        public void removeStock(int removedQty) {
            int currentStock = Integer.valueOf(this.stockQty); //sets currentstock and casts to int
            this.stockQty = Integer.toString((currentStock - removedQty)); // subtract then cast to string
        }
    } // end of catalogue class

    class cart {
        private catalogue item;
        private int numBought;
        private String customerID;

        public cart(catalogue i, int n, String c) {
            item = i;
            numBought = n;
            customerID = c;
        }

        public catalogue getCatalogue() {
            return item;
        }
        public int getNumBought() {
            return numBought;
        }
        public String getCustomerID() {
            return customerID;
        }
        public double getTotal() {
            return (numBought * Integer.valueOf(item.getPrice()));
        }
    }
