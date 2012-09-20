import java.util.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.List;


class room extends mapper {
        public String description = null;
        public String roomType = null;
        public String status = null;
        public List<String> item = new ArrayList<String>();
        public Borderer[] border = new Borderer[4];
        public List<String> creatures = new ArrayList<String>();
        public List<String> container = new ArrayList<String>();
        protected Triggerer[] trigger = new Triggerer[10];
        
        public room (String n, String t, String rT, String d, String st, List<String> i, List<String> c, Borderer[] b, Triggerer[] trig, List<String> zc) {
                super(n, t);
                description = d;
                roomType = rT;
                status = st;
                item = i;
                container = c;
                border = b;
                trigger = trig;
                creatures = zc;
        }
        
        
        public Triggerer hasCommandTrigger(String reqCommand) {
                for (int i = 0; i < this.trigger.length; i++) {
                        
                        if (this.trigger[i] != null
                                        && this.trigger[i].command != null
                                        && this.trigger[i].command.equals(reqCommand.split(" ")[0])) {
                                
                                if (this.trigger[i].type != null) {
                                        if (this.trigger[i].type.equals("single")) {
                                                if (this.trigger[i].hasBeenInvoked) {
                                                        
                                                        return null;
                                                }
                                                else {
                                                        
                                                        return this.trigger[i];
                                                }
                                        }
                                }
                                
                                return this.trigger[i];
                        }
                }
                return null;
        }
        
        
        public String zorkMove(String command) {
                
                if (this.border != null) {
                        for (int i = 0; i < this.border.length; i++) {
                                if (this.border[i] != null) {
                                        char direction = this.border[i].direction.toCharArray()[0];
                                        if (command.toCharArray()[0] == direction) {
                                                return this.border[i].name;
                                        }
                                }
                        }
                }
                return null;
        }
        
        public void addItem(String newItem, List<mapper> map) {
                
                if (((zorkItem) findObject(map, newItem, "item")) != null) {
                        this.item.add(newItem);
                }
                else if (((creature) findObject(map, newItem, "creature")) != null) {
                        this.creatures.add(newItem);
                }
        }
        
        public void dropItem(List<String> inventory, String itemToDrop) {
                if (inventory.contains(itemToDrop)) {
                        this.item.add(itemToDrop);
                        inventory.remove(itemToDrop);
                        System.out.println(itemToDrop + " dropped.");
                }
                else {
                        
                        System.out.println("Error");
                }
        }
        
        public boolean contains(String type, String seek) {
                
                if (type.equals("creature")) {
                        if (this.creatures.contains(seek)) {
                                return true;
                        }
                }
                else if (type.equals("container")) {
                        if (this.container.contains(seek)) {
                                return true;
                        }
                }
                return false;
        }
        
        public void detachBorder(String adjacentRoom) {
                for (int i = 0; i < this.border.length; i++) {
                        if (this.border[i] != null
                                        && this.border[i].name != null
                                        && this.border[i].name.equals(adjacentRoom)) {
                                this.border[i] = null;
                        }
                }
        }
}

class ZorkContain extends mapper {
    public String description = null;
    public List<String> item = null;
    public List<String> accept = null;
    public String status = null;
    protected Triggerer[] trigger;
    public boolean takeAble = false;
    
    public ZorkContain(String n, String t, String d, List<String> i, List<String> a, String s, Triggerer[] zt) {
            super(n, t);
            description = d;
            item = i;
            accept = a;
            status = s;
            trigger = zt;
            takeAble = false;
    }
   
    
    public void addItem(String newItem) {
            this.item.add(newItem);
    }
    
    public void open() {
            
            if (this.accept.size() > 0 && !this.takeAble) {
                    if (this.item.contains(this.accept)) {
                            this.takeAble = true;
                    }
                    else {
                            
                            System.out.println("Error");
                    }
            }
            
            else {
                    this.takeAble = true;
                    if (this.item.isEmpty()) {
                            System.out.println(this.name + " is empty.");
                    }
                    else {
                            System.out.println(this.name + " contains " + this.item.toString().replace("[", "").replace("]", "") + ".");
                    }
            }
    }
    
    public boolean take(List<String> inventory, String itemToTake) {
            if (this.takeAble == true && this.item.contains(itemToTake)) {
                    inventory.add(itemToTake);
                    this.item.remove(itemToTake);
                    return true;
            }
            return false;
    }
    
    public List<mapper> put(List<mapper> map, List<String> inventory, String newItem, String currentRoom) {
            if (this.accept.size() > 0) {
                    if (!this.accept.contains(newItem)) {
                            
                            System.out.println("Error");
                    }
                    else {
                            if (inventory.contains(newItem)) {
                                    this.item.add(newItem);
                                    inventory.remove(newItem);
                                    this.takeAble = true;
                                    System.out.println("Item " + newItem + " added to " + this.name + ".");
                                    
                            }
                            else {
                                    System.out.println("Error");
                            }
                    }
            }
            else {
                    if (this.takeAble == true) {
                            this.item.add(newItem);
                            if (!inventory.remove(newItem)) {
                                   
                                    System.out.println("Error");
                            }
                            else {
                                    System.out.println("Item " + newItem + " added to " + this.name + ".");
                            }
                    }
                    else {
                            
                            System.out.println("Error");
                    }
            }
            
            return map;
    }
    
    public Triggerer hasCommandTrigger(String reqCommand) {
            for (int i = 0; i < this.trigger.length; i++) {
                    if (this.trigger[i] != null
                                    && this.trigger[i].command != null
                                    && this.trigger[i].command.equals(reqCommand)) {
                            
                            if (this.trigger[i].type != null
                                            || this.trigger[i].type.equals("single")) {
                                    if (this.trigger[i].hasBeenInvoked) {
                                            return null;
                                    }
                                    else {
                                            return this.trigger[i];
                                    }
                            }
                            
                            return this.trigger[i];
                    }
            }
            return null;
    }
}

class Onner {
    public String print = null;
    public String[] action = null;
    
    public Onner (String p, String[] a) {
            print = p;
            action = a;
    }
    

}

class zorkItem extends mapper {
    public String writing = null;
    public String status = null;
    public Onner turnon = null;
    public Triggerer[] trigger = null;
    
    public zorkItem(String n, String t, String w, String s, Onner to, Triggerer[] zt) {
            super(n, t);
            writing = w;
            status = s;
            turnon = to;
            trigger = zt;
    }

    
    public unity activate(List<mapper> map, List<String> inventory, String currentRoom) {
            System.out.println("You activate the " + this.name + ".");
            if (this.turnon.print != null) {
                    System.out.println(this.turnon.print);
            }
            unity x = takeAction(map, this.turnon.action, inventory, currentRoom);
            
            return x;
    }
    
    public Triggerer hasCommandTrigger(String reqCommand) {
            if (this.trigger != null) {
                    for (int i = 0; i < this.trigger.length; i++) {
                            if (this.trigger[i] != null
                                            && this.trigger[i].command != null
                                            && this.trigger[i].command.equals(reqCommand)) {
                                    
                                    if (this.trigger[i].type != null
                                                    || this.trigger[i].type.equals("single")) {
                                            if (this.trigger[i].hasBeenInvoked) {
                                                    return null;
                                            }
                                            else {
                                                    return this.trigger[i];
                                            }
                                    }
                                    
                                    return this.trigger[i];
                            }
                    }
            }
            return null;
    }
}

class Borderer {
    public String direction;
    public String name;
    
    public Borderer(String d, String n) {
            direction = d;
            name = n;
    }

}
class mapper {
    
	public String name;
    public String type;
    
    public mapper(String n, String t) {
            name = n;
            type = t;
    }
    
    
    public static mapper findObject(List<mapper> mapContainer
                    , String identifier, String objectType) {
            boolean isFound = false;
            mapper soughtObject = null;
            ListIterator<mapper> i = mapContainer.listIterator();
            while (i.hasNext() && !isFound) {
                    mapper currentObject = i.next();
                    if (currentObject.name.equals(identifier) &&
                            currentObject.type.equals(objectType)) {
                            isFound = true;
                            soughtObject = currentObject;
                    }
            }
            
            return soughtObject;
    }
    
    public unity takeAction(List<mapper> map, String[] actionArray, List<String> inventory, String currentRoom) {
            for (int i = 0; i < actionArray.length; i++) {
                    if (actionArray[i] != null) {
                            String[] buttonComm = actionArray[i].split(" ");
                            if (buttonComm[0].equals("Update")) {
                                    
                                    if ((zorkItem) findObject(map, buttonComm[1], "item") != null) {
                                            
                                            ((zorkItem) findObject(map, buttonComm[1], "item")).status = buttonComm[3];
                                            
                                    }
                                    else if ((room) findObject(map, buttonComm[1], "room") != null) {
                                            ((room) findObject(map, buttonComm[1], "room")).status = buttonComm[3];
                                    }
                                    else if ((ZorkContain) findObject(map, buttonComm[1], "container") != null) {
                                            ((ZorkContain) findObject(map, buttonComm[1], "container")).status = buttonComm[3];
                                    }
                                    else if ((room) findObject(map, buttonComm[1], "creature") != null) {
                                            ((creature) findObject(map, buttonComm[1], "creature")).status = buttonComm[3];
                                    }
                                    
                                    
                            }
                            else if (buttonComm[0].equals("Add")) {
                                    
                                    if (buttonComm[3].equals("inventory")) {
                                            inventory.add(buttonComm[1]);
                                    }
                                    else if (((room) findObject(map, buttonComm[3], "room")) != null) {
                                            ((room) findObject(map, buttonComm[3], "room")).addItem(buttonComm[1], map);
                                    }
                                    
                                    else if (((ZorkContain) findObject(map, buttonComm[3], "container")) != null) {
                                            ((ZorkContain) findObject(map, buttonComm[3], "container")).addItem(buttonComm[1]);
                                    }
                            }
                            else if (buttonComm[0].equals("Delete")) {
                                    
                                    
                                    if (((room) findObject(map, currentRoom, "room")).container.contains(buttonComm[1])) {
                                            List<String> itemToDelete = ((ZorkContain) findObject(map, buttonComm[1], "container")).item;
                                            Iterator<String> itd = itemToDelete.iterator();
                                            while(itd.hasNext()) {
                                                    ((room) findObject(map, currentRoom, "room")).item.remove(itd.next());
                                            }
                                    }
                                    
                                    Iterator<mapper> t = map.listIterator();
                                    while (t.hasNext()) {
                                            mapper checkT = t.next();
                                            if (checkT.type.equals("room")) {
                                                    ((room) findObject(map, checkT.name, "room")).creatures.remove(buttonComm[1]);
                                                    ((room) findObject(map, checkT.name, "room")).item.remove(buttonComm[1]);
                                                    ((room) findObject(map, checkT.name, "room")).container.remove(buttonComm[1]);
                                                    ((room) findObject(map, checkT.name, "room")).detachBorder(buttonComm[1]);
                                            }
                                    }
                                    
                                    inventory.remove(buttonComm[1]);
                                    
                            }
                            else if (actionArray[i].equals("Game Over")) {
                                    System.out.println("Victory!");
                                    System.exit(0);
                            }
                            else if (buttonComm[0].equals("drop")) {
                                    
                                    if (inventory.contains(buttonComm[1])) {
                                            
                                            ((room) findObject(map, currentRoom, "room")).dropItem(inventory, buttonComm[1]);
                                    }
                            }
                            else if (buttonComm[0].equals("take")) {
                                    boolean itemFound = false;
                                    if (((room) findObject(map, currentRoom, "room")).item.contains(buttonComm[1])) {
                                            inventory.add(buttonComm[1]);
                                            ((room) findObject(map, currentRoom, "room")).item.remove(buttonComm[1]);
                                            itemFound = true;
                                    }
                                    else {
                                            List<String> containerInRoom = ((room) findObject(map, currentRoom, "room")).container;
                                            Iterator<String> icir = containerInRoom.listIterator();
                                            while (icir.hasNext()) {
                                                    String currentContainer = icir.next();
                                                    if (((ZorkContain) findObject(map, currentContainer, "container")).item.contains(buttonComm[1])
                                                                    && ((ZorkContain) findObject(map, currentContainer, "currentContainer")).takeAble) {
                                                            inventory.add(buttonComm[1]);
                                                            ((ZorkContain) findObject(map, currentContainer, "container")).item.remove(buttonComm[1]);
                                                            itemFound = true;
                                                    }
                                            }
                                    }
                                    
                                    if (!itemFound) {
                                            System.out.println("Error");
                                    }
                                    else {
                                            System.out.println("Item " 
                                                            + buttonComm[1] 
                                                            + " added to inventory.");
                                    }
                            }
                            else if (buttonComm[0].equals("open")) {
                                    
                                    if (!((room) findObject(map, currentRoom, "room")).contains("container", buttonComm[1])) {
                                            
                                            System.out.println("Error");
                                    }
                                    else {
                                            
                                            ((ZorkContain) findObject(map, buttonComm[1], "container")).open();
                                    }
                            }
                            else if (buttonComm[0].equals("put")) {
                                    if (((room) findObject(map, currentRoom, "room")).contains("container", buttonComm[3])) {
                                            map = ((ZorkContain) findObject(map, buttonComm[3], "container")).put(map, inventory, buttonComm[1], currentRoom);
                                    }
                                    else {
                                            
                                            System.out.println("Error");
                                    }
                            }
                            else if (buttonComm[0].matches("[nsew]")) {
                                    String testNextRoom = null;
                                    testNextRoom = ((room) findObject(map, currentRoom, "room")).zorkMove(buttonComm[0]);
                                    if ((room) findObject(map, testNextRoom, "room") == null) {
                                            System.out.println("Error");
                                    }
                                    else {
                                            currentRoom = testNextRoom;
                                            
                                            if (((room) findObject(map, currentRoom, "room")).description != null) {
                                                    System.out.println(((room) findObject(map, currentRoom, "room")).description);
                                            }
                                    }
                            }
                            else if (buttonComm[0].equals("attack")) {
                                    
                                    if (((room) findObject(map, currentRoom, "room")).contains("creature", buttonComm[1])) {
                                            
                                            if (inventory.contains(buttonComm[3])) {
                                                    unity x =
                                                            ((creature) findObject(map, buttonComm[1], "creature")).attacker(map, inventory, buttonComm[3], currentRoom);
                                                    map = x.map;
                                                    inventory = x.inventory;
                                                    currentRoom = x.currentRoom;
                                            }
                                            else {
                                                    
                                                    System.out.println("Error");
                                            }
                                    }
                                    else {
                                            
                                            System.out.println("Error");
                                    }
                            }
                            else if (buttonComm[0].equals("i")) {
                                    if ((inventory.toArray()).length > 0) {
                                            System.out.println("Inventory: " + (Arrays.toString(inventory.toArray())).replace("[", "").replace("]", ""));
                                    }
                                    else {
                                            System.out.println("Inventory: empty");
                                    }
                            }
                            else if (buttonComm[0].equals("read")) {
                                    if (inventory.contains(buttonComm[1])) {
                                            if (((zorkItem) findObject(map, buttonComm[1], "item")).writing != null) {
                                                    System.out.println(((zorkItem) findObject(map, buttonComm[1], "item")).writing);
                                            }
                                            else {
                                                    
                                                    System.out.println("Nothing written.");
                                            }
                                    }
                                    else {
                                            
                                            System.out.println("Error");
                                    }
                            }
                            else if (buttonComm[0].equals("turn")) {
                                    if (inventory.contains(buttonComm[2])
                                                    && ((zorkItem) findObject(map, buttonComm[2], "item")).turnon != null) {
                                            unity x = ((zorkItem) findObject(map, buttonComm[2], "item")).activate(map, inventory, currentRoom);
                                            map = x.map;
                                            inventory = x.inventory;
                                    }
                                    else {
                                            
                                            System.out.println("Error");
                                    }
                            }
                    }
            }
            
            unity x = findObject(map, currentRoom, "room").searchForTrigger(map, inventory, currentRoom);
            x.currentRoom = currentRoom;
            
            return x;
    }
    
    
    
    
    public boolean checkTrigger(String command, List<String> currentItem, List<mapper> map) {
            boolean result = false;
            if (this.type.equals("room")) {
                    if (((room) findObject(map, this.name, "room")).hasCommandTrigger(command) != null) {
                            result = (((room) findObject(map, this.name, "room")).hasCommandTrigger(command).checkCondition(currentItem, map));
                            
                    }
            }
            else if (this.type.equals("item")) {
                    if (((zorkItem) findObject(map, this.name, "item")).hasCommandTrigger(command) != null) {
                            result = (((zorkItem) findObject(map, this.name, "item")).hasCommandTrigger(command).checkCondition(currentItem, map));
                    }
            }
            else if (this.type.equals("container")) {
                    if (((ZorkContain) findObject(map, this.name, "container")).hasCommandTrigger(command) != null) {
                            result = (((ZorkContain) findObject(map, this.name, "container")).hasCommandTrigger(command).checkCondition(currentItem, map));
                    }
            }
            else if (this.type.equals("creature")) {
                    if (((creature) findObject(map, this.name, "creature")).hasCommandTrigger(command) != null) {
                            result = (((creature) findObject(map, this.name, "creature")).hasCommandTrigger(command).checkCondition(currentItem, map));
                            
                    }
            }
            return result;
    }
    
    public unity searchForTrigger(List<mapper> map, List<String> inventory, String currentRoom) {
            
            unity x = new unity(map, inventory, currentRoom);
            ListIterator<String> itemInspected = ((room) this).item.listIterator();
            boolean trigger = false;
            while (itemInspected.hasNext()) {
                    String elementInspected = itemInspected.next();
                    
                    if (((zorkItem) findObject(map, elementInspected, "item")) != null
                                    && ((zorkItem) findObject(map, elementInspected, "item")).trigger != null) {
                            Triggerer[] elementTrigger = ((zorkItem) findObject(map, elementInspected, "item")).trigger;
                            for (int i = 0; i < elementTrigger.length; i++) {
                                    if (elementTrigger[i] != null) {
                                            trigger = elementTrigger[i].checkConditionQuiet(inventory, map);
                                            if (trigger)  {
                                                    if (elementTrigger[i].type == null || elementTrigger[i].type.equals("single")) {
                                                            if (!elementTrigger[i].hasBeenInvoked) {
                                                                    if (elementTrigger[i].command == null) {
                                                                            
                                                                            elementTrigger[i].hasBeenInvoked = true;
                                                                            x = takeAction(map, elementTrigger[i].action, inventory, currentRoom);
                                                                    }
                                                            }
                                                    }
                                                    else {
                                                            if (elementTrigger[i].command == null) {
                                                                    
                                                                    elementTrigger[i].hasBeenInvoked = true;
                                                                    x = takeAction(map, elementTrigger[i].action, inventory, currentRoom);
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
            
            
            itemInspected = ((room) this).container.listIterator();
            trigger = false;
            while (itemInspected.hasNext()) {
                    String elementInspected = itemInspected.next();
                    if (((ZorkContain) findObject(map, elementInspected, "container")).trigger != null) {
                            Triggerer[] elementTrigger = ((ZorkContain) findObject(map, elementInspected, "container")).trigger;
                            for (int i = 0; i < elementTrigger.length; i++) {
                                    if (elementTrigger[i] != null) {
                                            trigger = elementTrigger[i].checkConditionQuiet(inventory, map);
                                            if (trigger)  {
                                                    if (elementTrigger[i].type == null || elementTrigger[i].type.equals("single")) {
                                                            if (!elementTrigger[i].hasBeenInvoked) {
                                                                    if (elementTrigger[i].command == null) {
                                                                            
                                                                            elementTrigger[i].hasBeenInvoked = true;
                                                                            x = takeAction(map, elementTrigger[i].action, inventory, currentRoom);
                                                                    }
                                                            }
                                                    }
                                                    else {
                                                            if (elementTrigger[i].command == null) {
                                                                    
                                                                    elementTrigger[i].hasBeenInvoked = true;
                                                                    x = takeAction(map, elementTrigger[i].action, inventory, currentRoom);
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
            
            
            itemInspected = ((room) this).creatures.listIterator();
            trigger = false;
            while (itemInspected.hasNext()) {
                    String elementInspected = itemInspected.next();
                    
                    if (((creature) findObject(map, elementInspected, "creature")).trigger != null) {
                            Triggerer[] elementTrigger = ((creature) findObject(map, elementInspected, "creature")).trigger;
                            for (int i = 0; i < elementTrigger.length; i++) {
                                    if (elementTrigger[i] != null) {
                                            trigger = elementTrigger[i].checkConditionQuiet(inventory, map);
                                            if (trigger)  {
                                                    if (elementTrigger[i].type == null || elementTrigger[i].type.equals("single")) {
                                                           
                                                            if (!elementTrigger[i].hasBeenInvoked) {
                                                                    
                                                                    if (elementTrigger[i].command == null) {
                                                                            
                                                                            elementTrigger[i].hasBeenInvoked = true;
                                                                            x = takeAction(map, elementTrigger[i].action, inventory, currentRoom);
                                                                    }
                                                            }
                                                    }
                                                    else {
                                                            if (elementTrigger[i].command == null) {
                                                                    
                                                                    elementTrigger[i].hasBeenInvoked = true;
                                                                    x = takeAction(map, elementTrigger[i].action, inventory, currentRoom);
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
            }
            return x;
            
    }
}
class Conditioner {
    public String has = null;
    public String object = null;
    public String owner = null;
    public String status = null;
    
    public Conditioner(String h, String obj, String own, String s) {
            has = h;
            object = obj;
            owner = own;
            status = s;
    }

    public boolean assess(List<String> inventory, List<mapper> map) {
            if (this.owner != null
                            && this.owner.equals("inventory")) {
                    
                    if (this.has != null && this.has.equals("yes")) {
                            
                            if (inventory.contains(this.object)) {
                                    return true;
                            }
                            else {
                                    return false;
                            }
                    }
                    else {
                            if (!inventory.contains(this.object)) {
                                    return true;
                            }
                            else {
                                    return false;
                            }
                    }
            }
            else {
                    if (this.has != null) {
                            if (((ZorkContain) findObject(map, this.owner, "container")) != null) {
                                    ZorkContain ciq = ((ZorkContain) findObject(map, this.owner, "container"));
                                    if (this.has.equals("yes")) {
                                            if (ciq.item.contains(this.object)) {
                                                    return true;
                                            }
                                            else {
                                                    return false;
                                            }
                                    }
                                    else {
                                            if (!ciq.item.contains(this.object)) {
                                                    return true;
                                            }
                                            else {
                                                    return false;
                                            }
                                    }
                            }
                            else {
                                    room riq = ((room) findObject(map, this.owner, "room"));
                                    if (this.has.equals("yes")) {
                                            if (riq.item.contains(this.object)) {
                                                    return true;
                                            }
                                            else {
                                                    return false;
                                            }
                                    }
                                    else {
                                            if (!riq.item.contains(this.object)){
                                                    return true;
                                            }
                                            else {
                                                    return false;
                                            }
                                    }
                            }
                    }
                    else {
                            if (((room) findObject(map, this.object, "room")) != null) {
                                    if (this.status.equals(((room) findObject(map, this.object, "room")).status)) {
                                            return true;
                                    }
                            }
                            else if (((zorkItem) findObject(map, this.object, "item")) != null) {
                                    if (this.status.equals(((zorkItem) findObject(map, this.object, "item")).status)) {
                                            return true;
                                    }
                            }
                            else if (((ZorkContain) findObject(map, this.object, "container")) != null) {
                                    if (this.status.equals(((ZorkContain) findObject(map, this.object, "container")).status)) {
                                            return true;
                                    }
                            }
                            else if (((creature) findObject(map, this.object, "creature")) != null) {
                                    if (this.status.equals(((creature) findObject(map, this.object, "creature")).status)) {
                                            return true;
                                    }
                            }
                            
                    }
            }
            return false;
    }
    
    public static mapper findObject(List<mapper> mapContainer
                    , String identifier, String objectType) {
            boolean isFound = false;
            mapper soughtObject = null;
            ListIterator<mapper> i = mapContainer.listIterator();
            while (i.hasNext() && !isFound) {
                    mapper currentObject = i.next();
                    if (currentObject.name.equals(identifier) &&
                            currentObject.type.equals(objectType)) {
                            isFound = true;
                            soughtObject = currentObject;
                    }
            }
            
            return soughtObject;
    }
}



class XMLReader {
    public XMLReader(List<mapper> mapContainer, String fileLocation) {
            try {
                    File file = new File(fileLocation);
                    DocumentBuilder builder =
                            DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = builder.parse(file);
                    
                    doc.getDocumentElement().normalize();
                    
                    Node map = doc.getElementsByTagName("map").item(0);
                    Node child = map.getFirstChild();
                    while (child != null) {
                            if (child.getNodeType() != Node.TEXT_NODE) {
                                    NodeList subchild = child.getChildNodes();
                                    
                                    if (child.getNodeName().equals("room")) {
                                        Borderer[] extracted = new Borderer[4];
                                        int index = 0;
                                        
                                        for (int i = 0; i < subchild.getLength(); i++) {
                                                if (subchild.item(i).getNodeType() != Node.TEXT_NODE
                                                                && subchild.item(i).getNodeName().equals("border")) {
                                                        NodeList border = subchild.item(i).getChildNodes();
                                                        extracted[index++] = new Borderer(XMLGetValue(border, "direction")
                                                                                                                        , XMLGetValue(border, "name"));
                                                }
                                        }
                                        
                                    	 room tempRoom = new room(XMLGetValue(subchild, "name")
                                                 , ((Node) subchild).getNodeName()
                                                 , XMLGetValue(subchild, "type")
                                                 , XMLGetValue(subchild, "description")
                                                 , XMLGetValue(subchild, "status")
                                                 , XMLGetList(subchild, "item")
                                                 , XMLGetList(subchild, "container")
                                                 , extracted
                                                 , XMLAttachTrigger(subchild)
                                                 , XMLGetList(subchild, "creature"));
                                    	 mapContainer.add(tempRoom);
                                    }
                                    else if (child.getNodeName().equals("item")) {
                                        String tPrint = null;
                                        String tAction[] = new String[10];
                                        int actionCounter = 0;
                                        Onner to = null;
                                        boolean hasTurnOn = false;
                                        
                                        for (int i = 0; i < subchild.getLength() && !hasTurnOn; i++) {
                                                if (subchild.item(i).getNodeType() != Node.TEXT_NODE
                                                                && (subchild.item(i).getNodeName().equals("turnon"))) {
                                                        hasTurnOn = true;
                                                        NodeList turnOnNode = subchild.item(i).getChildNodes();
                                                        for (int j = 0; j < turnOnNode.getLength(); j++) {
                                                                if (turnOnNode.item(j).getNodeType() != Node.TEXT_NODE) {
                                                                        if (turnOnNode.item(j).getNodeName().equals("print")) {
                                                                                tPrint = turnOnNode.item(j).getTextContent();
                                                                        }
                                                                        else if (turnOnNode.item(j).getNodeName().equals("action")) {
                                                                                tAction[actionCounter++] = turnOnNode.item(j).getTextContent();
                                                                        }
                                                                }
                                                        }
                                                        to = new Onner(tPrint, tAction);
                                                }
                                        }
                                        
                                        zorkItem tempItem = new zorkItem(XMLGetValue(subchild, "name")
                                                , ((Node) subchild).getNodeName()
                                                , XMLGetValue(subchild, "writing")
                                                , XMLGetValue(subchild, "status")
                                                , to
                                                , XMLAttachTrigger(subchild));
                                        mapContainer.add(tempItem);
                                    }
                                    else if (child.getNodeName().equals("container")) {
                                    	ZorkContain tempContainer = new ZorkContain(XMLGetValue(subchild, "name")
                                                , ((Node) subchild).getNodeName()
                                                , XMLGetValue(subchild, "description")
                                                , XMLGetList(subchild, "item")
                                                , XMLGetList(subchild, "accept")
                                                , XMLGetValue(subchild, "status")
                                                , XMLAttachTrigger(subchild));
                                    	mapContainer.add(tempContainer);
                                    }
                                    else if (child.getNodeName().equals("creature")) {
                                    	attacker temp = null;
                                        Conditioner[] creatureCondition = new Conditioner[10];
                                        String print = null;
                                        String[] action = new String[10];
                                        int actionCount = 0;
                                        int conditionCount = 0;
                                        boolean hasattacker = false;
                                        
                                        for (int i = 0; i < subchild.getLength() && !hasattacker; i++) {
                                                if (subchild.item(i).getNodeType() != Node.TEXT_NODE
                                                                && (subchild.item(i).getNodeName().equals("attack"))) {
                                                        hasattacker = true;
                                                        NodeList attackerNode = subchild.item(i).getChildNodes();
                                                        for (int j = 0; j < attackerNode.getLength(); j++) {
                                                                if (attackerNode.item(j).getNodeName().equals("condition")) {
                                                                        creatureCondition[conditionCount++] = XMLAttachCondition(attackerNode.item(j).getChildNodes());
                                                                }
                                                                else if (attackerNode.item(j).getNodeName().equals("print")) {
                                                                        print = attackerNode.item(j).getTextContent();
                                                                }
                                                                else if (attackerNode.item(j).getNodeName().equals("action")) {
                                                                        action[actionCount++] = attackerNode.item(j).getTextContent();
                                                                }
                                                        }
                                                        temp = new attacker(creatureCondition, print, action);
                                                }
                                        }
                                                                              
                                       
                                    	creature temp1 = new creature(XMLGetValue(subchild, "name")
                                                , ((Node) subchild).getNodeName()
                                                , XMLGetValue(subchild, "description")
                                                , XMLGetList(subchild, "vulnerability")
                                                , XMLGetValue(subchild, "status")
                                                , temp
                                                , XMLAttachTrigger(subchild));
                                    	mapContainer.add(temp1);
                                    }
                            }
                            child = child.getNextSibling();
                    }
            }
            catch (Exception e) {
                    System.out.println("User defined exception caught: ");
                    e.printStackTrace();
            }
    }
    
    private Triggerer[] XMLAttachTrigger(NodeList parent) {
            int triggerCount = 0;
            String tType = null;
            String tCommand = null;
            String[] tAction = new String[10];
            int actionCount = 0;
            String tPrint = null;
            Triggerer[] temp = new Triggerer[10];
            Conditioner[] cond = new Conditioner[10];
            int conditionCount = 0;
            
            for (int i = 0; i < parent.getLength(); i++) {
                    if (parent.item(i).getNodeType() != Node.TEXT_NODE
                                    && (parent.item(i).getNodeName().equals("trigger"))) {
                            NodeList triggerNode = parent.item(i).getChildNodes();
                            cond = new Conditioner[10];
                            conditionCount = 0;
                            for (int j = 0; j < triggerNode.getLength(); j++) {
                                    if (triggerNode.item(j).getNodeType() != Node.TEXT_NODE) {
                                            if (triggerNode.item(j).getNodeName().equals("type")) {
                                                    tType = triggerNode.item(j).getTextContent();
                                            }
                                            else if (triggerNode.item(j).getNodeName().equals("command")) {
                                                    tCommand = triggerNode.item(j).getTextContent();
                                            }
                                            else if (triggerNode.item(j).getNodeName().equals("action")) {
                                                    tAction[actionCount++] = triggerNode.item(j).getTextContent();
                                            }
                                            else if (triggerNode.item(j).getNodeName().equals("print")) {
                                                    tPrint = triggerNode.item(j).getTextContent();
                                            }
                                            else if (triggerNode.item(j).getNodeName().equals("condition")) {
                                                    cond[conditionCount++] = XMLAttachCondition(triggerNode.item(j).getChildNodes());
                                            }
                                    }
                            }
                            temp[triggerCount++] = new Triggerer(tType, tCommand, cond, tPrint, tAction);
                    }
            }
            return temp;
    }

    private Conditioner XMLAttachCondition(NodeList condition) {
            String has = null;
            String object = null;
            String owner = null;
            String status = null;
            for (int i = 0; i < condition.getLength(); i++) {
                    if (condition.item(i).getNodeName().equals("has")) {
                            has = condition.item(i).getTextContent();
                    }
                    else if (condition.item(i).getNodeName().equals("object")) {
                            object = condition.item(i).getTextContent();
                    }
                    else if (condition.item(i).getNodeName().equals("owner")) {
                            owner = condition.item(i).getTextContent();
                    }
                    else if (condition.item(i).getNodeName().equals("status")) {
                            status = condition.item(i).getTextContent();
                    }
            }
            
            Conditioner temp = new Conditioner(has, object, owner, status);
            
            return temp;
    }


    private String XMLGetValue(NodeList parent, String seek) {
            boolean isFound = false;
            String extracted = null;
            
            for (int i = 0; i < parent.getLength() && !isFound; i++) {
                    if (parent.item(i).getNodeType() != Node.TEXT_NODE
                                    && parent.item(i).getNodeName() == seek) {
                            isFound = true;
                            extracted = parent.item(i).getTextContent();
                    }
            }
            
            return extracted;
    }

    private List<String> XMLGetList(NodeList parent, String seek) {
            List<String> extracted = new ArrayList<String>();
            
            for (int i = 0; i < parent.getLength(); i++) {
                    if (parent.item(i).getNodeType() != Node.TEXT_NODE
                                    && parent.item(i).getNodeName().equals(seek)) {
                            extracted.add(parent.item(i).getTextContent());
                    }
            }
            
            return extracted;
    }
}

class Triggerer {
        public String type = null;
        public String command = null;
        public Conditioner[] condition;
        public String description = null;
        public String[] action = null;
        public boolean hasBeenInvoked = false;
        
        public Triggerer(String t, String c, Conditioner[] zc, String d, String[] a) {
                type = t;
                command = c;
                condition = zc;
                description = d;
                action = a;
        }

        
        public boolean checkCondition(List<String> inventory, List<mapper> map) {
                boolean result = false;
                if (this.condition != null) {
                        for (int i = 0; i < this.condition.length; i++) {
                                if (this.condition[i] != null) {
                                        result = this.condition[i].assess(inventory, map);
                                        if (this.type == null || this.type.equals("single")) {
                                                if (!this.hasBeenInvoked && result) {
                                                        this.hasBeenInvoked = true;
                                                        if (result && this.description != null) {
                                                                System.out.println(this.description);
                                                                return result;
                                                        }
                                                        else if (result) {
                                                                return result;
                                                        }
                                                }
                                        }
                                        else {
                                                if (result && this.description != null) {
                                                        this.hasBeenInvoked = true;
                                                        System.out.println(this.description);
                                                        return result;
                                                }
                                                else if (result) {
                                                        this.hasBeenInvoked = true;
                                                        return result;
                                                }
                                        }
                                }
                        }
                }
                return result;
        }
        
        public boolean checkConditionQuiet(List<String> inventory, List<mapper> map) {
                boolean result = false;
                if (this.condition != null) {
                        for (int i = 0; i < this.condition.length; i++) {
                                if (this.condition[i] != null && this.command == null) {
                                        result = this.condition[i].assess(inventory, map);
                                        if (this.type == null || this.type.equals("single")) {
                                                if (!this.hasBeenInvoked && result) {
                                                        if (result && this.description != null) {
                                                                if (this.command == null) {
                                                                        System.out.println(this.description);
                                                                }
                                                                return result;
                                                        }
                                                        else if (result) {
                                                                return result;
                                                        }
                                                }
                                        }
                                        else {
                                                if (result && this.description != null) {
                                                        this.hasBeenInvoked = true;
                                                        return result;
                                                }
                                                else if (result) {
                                                        this.hasBeenInvoked = true;
                                                        return result;
                                                }
                                        }
                                }
                        }
                }
                return result;
        }
        

}

class attacker extends Triggerer {
    
    public attacker(Conditioner[] c, String p, String[] a) {
            super(null, null, c, p, a);
            
    }
}

class unity {
    public List<mapper> map;
    public List<String> inventory;
    public String currentRoom;
    public unity(List<mapper> m, List<String> i, String cr) {
            map = m;
            inventory = i;
            currentRoom = cr;
    }
}
class creature extends mapper {
    public List<String> vulnerability = null;
    public String description = null;
    public String status = null;
    public attacker attacker = null;
    public Triggerer[] trigger;
    
    public creature(String n, String t, String d, List<String> v, String st, attacker a, Triggerer[] zt) {
            super(n, t);
            description = d;
            vulnerability = v;
            status = st;
            attacker = a;
            trigger = zt;
    }

    
    public unity attacker(List<mapper> map, List<String> item, String weapon, String currentRoom) {
            unity x = new unity(map, item, currentRoom);
            boolean noCondition = false;
            if (this.vulnerability.contains(weapon)) {
                   
                    if (this.attacker != null) {
                            for (int i = 0; i < this.attacker.condition.length; i++) {
                                    if (i == this.attacker.condition.length - 1 && this.attacker.condition[i] == null) {
                                            System.out.println("You assault the " + this.name + " with the " + weapon + ".");
                                            if (this.attacker.description != null) {
                                                    System.out.println(this.attacker.description);
                                            }
                                            x = takeAction(map, this.attacker.action, item, currentRoom);
                                            noCondition = true;
                                    }
                                    else if (this.attacker.condition[i] != null) {
                                            break;
                                    }
                            }
                            int checkey=0;
                            if (!noCondition) {
                                boolean result = false;
                                if (this.attacker.condition != null && checkey!=1) {
                                        for (int i = 0; i < this.attacker.condition.length; i++) {
                                                if (this.attacker.condition[i] != null && this.attacker.command == null) {
                                                        result = this.attacker.condition[i].assess(item, map);
                                                        if (this.attacker.type == null || this.attacker.type.equals("single")) {
                                                                if (!this.attacker.hasBeenInvoked && result) {
                                                                        if (result && this.attacker.description != null) {
                                                                        	checkey=1;
                                                                                break ;
                                                                                
                                                                        }
                                                                        else if (result) {
                                                                        	checkey=1;
                                                                                break ;
                                                                        }
                                                                }
                                                        }
                                                        else {
                                                                if (result && this.attacker.description != null) {
                                                                        this.attacker.hasBeenInvoked = true;
                                                                        checkey=1;
                                                                        break ;
                                                                }
                                                                else if (result) {
                                                                        this.attacker.hasBeenInvoked = true;
                                                                        checkey=1;
                                                                        break ;
                                                                }
                                                        }
                                                }
                                        }
                                }
                                
                        
                                    if (result) {
                                            System.out.println("You assault the " + this.name + " with the " + weapon + ".");
                                            if (this.attacker.description != null) {
                                                    System.out.println(this.attacker.description);
                                            }
                                            x = takeAction(map, this.attacker.action, item, currentRoom);
                                    }
                                    else {
                                            System.out.println("Error");
                                    }
                            }
                    }
                    else {
                            System.out.println("You assault the " + this.name + " with the " + weapon + ".");
                    }
            }
            else {
                    System.out.println("Error");
            }
            return x;
    }
    
    public Triggerer hasCommandTrigger(String reqCommand) {
            for (int i = 0; i < this.trigger.length; i++) {
                    if (this.trigger[i] != null
                                    && this.trigger[i].command != null
                                    && this.trigger[i].command.equals(reqCommand)) {
                            if (this.trigger[i].type != null
                                            || this.trigger[i].type.equals("single")) {
                                    if (this.trigger[i].hasBeenInvoked) {
                                            return null;
                                    }
                                    else {
                                            return this.trigger[i];
                                    }
                            }
                            return this.trigger[i];
                    }
            }
            return null;
    }
}


public class IPA1 {


        public static void main(String[] args) {
                try {
                        if (args.length >= 1) {
                                String xmlFile = args[0];
                                List<mapper> mapContainer = new ArrayList<mapper>();
                                new XMLReader(mapContainer, xmlFile);
                                playGame(mapContainer);
                        }
                        else {
                                System.out.println("No Map specified!");
                                System.exit(1);
                        }
                        
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private static void playGame(List<mapper> mapContainer) {
                String currentRoom = null;
                List<String> currentItem = new ArrayList<String>();
                String command = null;
                boolean exitFound = false;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                boolean overridden = false;
                
                currentRoom = zorkCheckEntrance(mapContainer);
                
                while (!exitFound) {
                        try {
                                command = br.readLine();
                                List<String> contextSearch = new ArrayList<String>();
                                Iterator<String> contextI = ((room) findObject(mapContainer, currentRoom, "room")).creatures.listIterator();
                                while (contextI.hasNext()) {
                                        contextSearch.add(contextI.next());
                                }
                                contextI = ((room) findObject(mapContainer, currentRoom, "room")).container.listIterator();
                                while (contextI.hasNext()) {
                                        contextSearch.add(contextI.next());
                                }
                                contextI = ((room) findObject(mapContainer, currentRoom, "room")).item.listIterator();
                                while (contextI.hasNext()) {
                                        contextSearch.add(contextI.next());
                                }
                                contextSearch.add(currentRoom);
                                Iterator<String> contextExecute = contextSearch.listIterator();
                                while (contextExecute.hasNext()) {
                                        String currentContext = contextExecute.next();
                                        
                                        if (((room) findObject(mapContainer, currentContext, "room")) != null) {
                                                overridden = ((room) findObject(mapContainer, currentContext, "room")).checkTrigger(command, currentItem, mapContainer);
                                                if (overridden && ((room) findObject(mapContainer, currentContext, "room")).trigger[0] != null) {
                                                        unity x = findObject(mapContainer, currentContext, "room").takeAction(mapContainer
                                                                        , ((room) findObject(mapContainer, currentContext, "room")).trigger[0].action
                                                                        , currentItem
                                                                        , currentRoom);
                                                        currentItem = x.inventory;
                                                        currentRoom = x.currentRoom;
                                                        mapContainer = x.map;
                                                }
                                        }
                                        else if (((zorkItem) findObject(mapContainer, currentContext, "item")) != null) {
                                                overridden = ((zorkItem) findObject(mapContainer, currentContext, "item")).checkTrigger(command, currentItem, mapContainer);
                                                if (overridden && ((zorkItem) findObject(mapContainer, currentContext, "item")).trigger[0] != null) {
                                                        unity x = findObject(mapContainer, currentContext, "item").takeAction(mapContainer
                                                                        , ((zorkItem) findObject(mapContainer, currentContext, "item")).trigger[0].action
                                                                        , currentItem
                                                                        , currentRoom);
                                                        currentItem = x.inventory;
                                                        currentRoom = x.currentRoom;
                                                        mapContainer = x.map;
                                                }
                                        }
                                        else if (((ZorkContain) findObject(mapContainer, currentContext, "container")) != null) {
                                                overridden = ((ZorkContain) findObject(mapContainer, currentContext, "container")).checkTrigger(command, currentItem, mapContainer);
                                                if (overridden && ((ZorkContain) findObject(mapContainer, currentContext, "container")).trigger[0] != null) {
                                                        unity x = findObject(mapContainer, currentContext, "container").takeAction(mapContainer
                                                                        , ((ZorkContain) findObject(mapContainer, currentContext, "container")).trigger[0].action
                                                                        , currentItem
                                                                        , currentRoom);
                                                        currentItem = x.inventory;
                                                        currentRoom = x.currentRoom;
                                                        mapContainer = x.map;
                                                }
                                        }
                                        else if (((creature) findObject(mapContainer, currentContext, "creature")) != null) {
                                                overridden = ((creature) findObject(mapContainer, currentContext, "creature")).checkTrigger(command, currentItem, mapContainer);
                                                if (overridden && ((creature) findObject(mapContainer, currentContext, "creature")).trigger[0] != null) {
                                                        unity x = findObject(mapContainer, currentContext, "creature").takeAction(mapContainer
                                                                        , ((creature) findObject(mapContainer, currentContext, "creature")).trigger[0].action
                                                                        , currentItem
                                                                        , currentRoom);
                                                        currentItem = x.inventory;
                                                        currentRoom = x.currentRoom;
                                                        mapContainer = x.map;
                                                }
                                        }
                                }
                                
                                
                                if (command.matches("^[nsew]$")) {
                                        
                                        if (!overridden) {
                                                
                                                if (((room) findObject(mapContainer, currentRoom, "room")).zorkMove(command) == null) {
                                                        System.out.println("Can't go that way.");
                                                }
                                                else {
                                                        String testNextRoom = null;
                                                        testNextRoom = ((room) findObject(mapContainer, currentRoom, "room")).zorkMove(command);
                                                        if ((room) findObject(mapContainer, testNextRoom, "room") == null) {
                                                                
                                                                System.out.println("Error");
                                                        }
                                                        else {
                                                                currentRoom = testNextRoom;
                                                                
                                                                if (((room) findObject(mapContainer, currentRoom, "room")).description != null) {
                                                                        System.out.println(((room) findObject(mapContainer, currentRoom, "room")).description);
                                                                }
                                                        }
                                                }
                                        }
                                        
                                }
                                
                                else if (command.matches("^[i]$")) {
                                       
                                        if ((currentItem.toArray()).length > 0) {
                                                System.out.println("Inventory: " + (Arrays.toString(currentItem.toArray())).replace("[", "").replace("]", ""));
                                        }
                                        else if (!overridden) {
                                                System.out.println("Inventory: empty");
                                        }
                                }
                                
                                else if (command.matches("^(take).*")) {
                                        if (command.split(" ").length != 2) {
                                                
                                                System.out.println("Error");
                                        }
                                        else if (!overridden) {
                                                boolean itemFound = false;
                                                if (((room) findObject(mapContainer, currentRoom, "room")).item.contains(command.split(" ")[1])) {
                                                        currentItem.add(command.split(" ")[1]);
                                                        ((room) findObject(mapContainer, currentRoom, "room")).item.remove(command.split(" ")[1]);
                                                        itemFound = true;
                                                }
                                                else {
                                                        List<String> containersInRoom = ((room) findObject(mapContainer, currentRoom, "room")).container;
                                                        ListIterator<String> i = containersInRoom.listIterator(); 
                                                        while (i.hasNext() && !itemFound) {
                                                                String inspection = i.next();
                                                                if (((ZorkContain) findObject(mapContainer, inspection, "container")).item != null
                                                                                && ((ZorkContain) findObject(mapContainer, inspection, "container")).item.contains(command.split(" ")[1])) {
                                                                        if (!((ZorkContain) findObject(mapContainer, inspection, "container")).take(currentItem, command.split(" ")[1])) {
                                                                        }
                                                                        else {
                                                                                itemFound = true;
                                                                        }
                                                                }
                                                        }
                                                }
                                                
                                                if (!itemFound) {
                                                        
                                                        System.out.println("Error");
                                                }
                                                else {
                                                        System.out.println("Item " 
                                                                        + command.split(" ")[1] 
                                                                        + " added to inventory.");
                                                }
                                        }
                                }
                                
                                else if (command.matches("^(open).*")) {
                                        if (command.split(" ").length != 2) {
                                                System.out.println("Error");
                                        }
                                        else if (!overridden) {
                                                if (command.trim().equals("open exit")) {
                                                        if (((room) findObject(mapContainer, currentRoom, "room")).roomType != null
                                                                        && ((room) findObject(mapContainer, currentRoom, "room")).roomType.equals("exit")) {
                                                                exitFound = true;
                                                        }
                                                        else {
                                                                System.out.println("Error");
                                                        }
                                                }
                                                else if (!((room) findObject(mapContainer, currentRoom, "room")).contains("container", command.split(" ")[1])) {
                                                        System.out.println("Error");
                                                }
                                                else {
                                                        ((ZorkContain) findObject(mapContainer, command.split(" ")[1], "container")).open();
                                                }
                                        }
                                }
                                
                                else if (command.matches("^(read).*")) {
                                        if (command.split(" ").length != 2) {
                                                System.out.println("Incorrect command. Usage: read [inventory]");
                                        }
                                        else if (!overridden) {
                                                if (currentItem.contains(command.split(" ")[1])) {
                                                        if (((zorkItem) findObject(mapContainer, command.split(" ")[1], "item")).writing != null) {
                                                                System.out.println(((zorkItem) findObject(mapContainer, command.split(" ")[1], "item")).writing);
                                                        }
                                                        else {
                                                                System.out.println("Nothing written.");
                                                        }
                                                }
                                                else {
                                                        System.out.println("Error");
                                                }
                                        }
                                }
                                
                                else if (command.matches("^(drop).*")) {
                                        if (command.split(" ").length != 2) {
                                                System.out.println("Error");
                                        }
                                        else if (!overridden) {
                                                ((room) findObject(mapContainer, currentRoom, "room")).dropItem(currentItem, command.split(" ")[1]);
                                        }
                                }
                                
                                else if (command.matches("^(put).*")) {
                                        if (command.split(" ").length != 4) {
                                                System.out.println("Error");
                                        }
                                        else if (!overridden) {
                                                if (((room) findObject(mapContainer, currentRoom, "room")).contains("container", command.split(" ")[3])) {
                                                        mapContainer = ((ZorkContain) findObject(mapContainer, command.split(" ")[3], "container")).put(mapContainer, currentItem, command.split(" ")[1], currentRoom);
                                                }
                                                else {
                                                        System.out.println("Error");
                                                }
                                        }
                                }
                                
                                else if (command.matches("^(turn[ ]?on).*")) {
                                        if (command.split(" ").length != 3) {
                                                System.out.println("Error");
                                        }
                                        else if (!overridden) {
                                                if (currentItem.contains(command.split(" ")[2])
                                                                && ((zorkItem) findObject(mapContainer, command.split(" ")[2], "item")).turnon != null) {
                                                        unity x = ((zorkItem) findObject(mapContainer, command.split(" ")[2], "item")).activate(mapContainer, currentItem, currentRoom);
                                                        mapContainer = x.map;
                                                        currentItem = x.inventory;
                                                }
                                                else {
                                                        System.out.println("Error");
                                                }
                                        }
                                }
                                
                                else if (command.matches("^(attack).*")) {
                                        if (command.split(" ").length != 4) {
                                                System.out.println("Error");
                                        }
                                        else if (!overridden) {
                                                if (((room) findObject(mapContainer, currentRoom, "room")).contains("creature", command.split(" ")[1])) {
                                                        if (currentItem.contains(command.split(" ")[3])) {
                                                                unity x =
                                                                        ((creature) findObject(mapContainer, command.split(" ")[1], "creature")).attacker(mapContainer, currentItem, command.split(" ")[3], currentRoom);
                                                                mapContainer = x.map;
                                                                currentItem = x.inventory;
                                                                currentRoom = x.currentRoom;
                                                        }
                                                        else {
                                                                System.out.println("Error");
                                                        }
                                                }
                                                else {
                                                        System.out.println("Error");
                                                }
                                        }
                                }
                                else if (command.matches("^(exit|quit|stop)$")) {
                                        System.out.println("Exiting game...");
                                        System.out.println("Goodbye!");
                                        break;
                                }
                                else {
                                        System.out.println("Error");
                                }
                                
                                if (!overridden) {
                                        unity x = ((mapper) findObject(mapContainer, currentRoom, "room")).searchForTrigger(mapContainer, currentItem, currentRoom);
                                        mapContainer = x.map;
                                        currentItem = x.inventory;
                                        
                                        
                                }
                        } catch (IOException e) {
                                System.out.println("Input recognition exception caught: ");
                                e.printStackTrace();
                        }
                }
                if (exitFound) {
                        System.out.println("Game Over");
                }
        }

        private static String zorkCheckEntrance(List<mapper> mapContainer) {
                boolean isFound = false;
                ListIterator<mapper> iterator = mapContainer.listIterator();
                while (iterator.hasNext() && !isFound) {
                        if ((iterator.next()).name.equals("Entrance")) {
                                iterator.previous();
                                System.out.println(((room) iterator.next()).description);
                                isFound = true;
                        }
                }
                
                if (!isFound) {
                        System.out.println("Room \"Entrance\" is not found in the given map");
                        System.exit(0);
                }
                return "Entrance";
        }

        public static mapper findObject(List<mapper> mapContainer
                        , String identifier, String objectType) {
                boolean isFound = false;
                mapper soughtObject = null;
                ListIterator<mapper> i = mapContainer.listIterator();
                while (i.hasNext() && !isFound) {
                        mapper currentObject = i.next();
                        if (currentObject.name.equals(identifier) &&
                                currentObject.type.equals(objectType)) {
                                isFound = true;
                                soughtObject = currentObject;
                        }
                }
                
                return soughtObject;
        }
}
