import java.util.Scanner;
//import java.util.Stack;
import lib.*;
public class Room implements Comparable{
        //Instance variables
        private Tile[][] _room;
        private Tile _playerTile;
        private int _row;
        private int _col;
        private Player _player;
        private boolean _isDone; //to check if room is completed.
        private ArrayQueue<Monster> _monsterQueue; //a queue of multiple or one monster
        private ArrayQueue<Tile> _monsterTileQueue;

        //constructor for a not same dimension room (length != width)
        public Room(int row, int column){
                //creates new player
                _player = new Player();
                //creates the room
                _room = new Tile[row][column];
                //just some holder variables
                _row = row;
                _col = column;
                _isDone = false;

                //creates the room
                for(int r = 0; r < _room.length; r++){
                        for(int c = 0; c < _room[r].length; c++){
                                if(r==0||r==_room.length-1||c==0||c==_room[0].length-1)
                                _room[r][c] = new Tile("wall",r,c);
                                else _room[r][c] = new Tile(r,c);
                        }
                }
                _playerTile = new Tile(_player, 1,1);
                _room[1][1] = _playerTile;
                //create a new monster(s)

                int randMonNum = 1 + ((int) (Math.random() * (row-2) * (column-2) * .25));
                _monsterQueue = new ArrayQueue<Monster>(randMonNum);
                _monsterTileQueue = new ArrayQueue<Tile>(randMonNum);
                for (int i = 0; i <  randMonNum; i++){
                        //int ranHealth = (int) (Math.random() * 10) * 10;
                        //int ranAttack = (int) (Math.random() * 10) * 2;
                        Monster enemy = new Monster();
                        Tile enemyTile = _room[1 + (int)(Math.random() * (row-2))][1 + (int) (Math.random() * (column - 2))];
                        while(!(enemyTile.getEntity() == null)){
                                enemyTile = _room[1 + (int) ((Math.random()) * (row-2))][1 + (int) (Math.random() * (column - 2))];
                        }
                        enemyTile.setEntity(enemy);
                        _monsterTileQueue.enqueue(enemyTile);
                        //_monsterQueue.enqueue(enemy);
                }

                //_playerTile.setEntity(_player);
        }

        //constructor for a square room
        public Room(int square){
                this(square, square);
        }

        //displays the room
        public void print(){
                for(Tile[] row : _room){
                        for(Tile pos: row){
                                System.out.print(pos.getChar());
                        }
                        System.out.println();
                }
        }

        //get player
        public Player getPlayer(){
                return (Player) (_playerTile.getEntity());
        }
        //update the current Player stats into the room.
        public void updatePlayer(Player player){
                _player = player;
        }

        //return if the Room is done.
        public Boolean checkDone(){
                return _isDone;
        }

        public void setDone(Boolean value){
                _isDone = value;
        }
        //asks a player to move
        public void askPlayerMove(){
                Scanner s = new Scanner(System.in);
                System.out.println("Input a direction");
                String input = s.next();
                //Check if a direction is inputed.
                //If not loop again3
                while ("wasd".indexOf(input) < 0){
                        if(input.equals("end")){
                                throw new BindingOfRugException("Ignore this error.It is stop the game! Have a nice day!");
                        }
                        System.out.println("Invalid direction. Input a direction as w,a,s,or d.");
                        input = s.next();

                }
                checkMove(input);
        }

        /*
        CHANGE TILES THING
        */
        public void message(String message){
                System.out.println(message);
        }

        public Boolean isValidMove(String input){
                int row = _playerTile.getRow();
                int col = _playerTile.getCol();
                Tile destination = _playerTile;

                if (input.equals("w")){
                        if (row - 1 < 0)
                        return false;
                        //destination =  _room[row - 1][col];
                }
                if (input.equals("a")){
                        //destination = _room[row][col - 1];
                        if (col - 1 < 0)
                        return false;
                }
                if(input.equals("s")){
                        //destination = _room[row + 1][col];
                        if (row + 1 > _row - 1)
                        return false;
                }
                if(input.equals("d")){
                        //destination = _room[row][col + 1];
                        if (col + 1 > _col - 1)
                        return false;
                }
                if (destination.getRow() < 0 || destination.getRow() > _row ||
                destination.getCol() < 0 || destination.getCol() > _col){
                        return false;
                }
                return true;
        }
        public void checkMove(String input){
                int row = _playerTile.getRow();
                int col = _playerTile.getCol();
                if(input.indexOf("x") == 0){
                        //((Player)(_room[row][col].getEntity())).checkAttack();
                        input = input.substring(1,2);
                        if (!isValidMove(input)){
                                message("You cannot move there");
                        }
                        else if (input.equals("w")){
                                attack(_room[row][col], _room[row - 1][col]);
                        }
                        else if (input.equals("a")){
                                attack(_room[row][col], _room[row][col - 1]);
                        }
                        else if(input.equals("s")){
                                attack(_room[row][col], _room[row + 1][col]);
                        }
                        else{ //when input is "d"
                                attack(_room[row][col], _room[row][col + 1]);
                        }
                }
                else if (!isValidMove(input)){
                        message("You cannot move there");
                }
                else if (input.equals("w")){
                        move(_room[row][col], _room[row - 1][col]);
                }
                else if (input.equals("a")){
                        move(_room[row][col], _room[row][col - 1]);
                }
                else if(input.equals("s")){
                        move(_room[row][col], _room[row + 1][col]);
                }
                else{ //when input is "d"
                        move(_room[row][col], _room[row][col + 1]);
                }
        }

        public void move(Tile origin, Tile destination){
                //GOAL: swap the tiles + check walls + check if out of bounds

                //Check if out of bound
                if (destination.getRow() < 0 || destination.getRow() > _row ||
                destination.getCol() < 0 || destination.getCol() > _col){
                        return;
                }

                //Check if destination is empty
                if(destination.getChar() == "_"){
                        //SWAP ENTITIES
                        int oRow = origin.getRow();
                        int oCol = origin.getCol();
                        int dRow = destination.getRow();
                        int dCol = destination.getCol();

                        Creature temp = origin.getEntity();
                        _room[oRow][oCol].setEntity(destination.getEntity());
                        _room[dRow][dCol].setEntity(temp);
                        /*
                        origin.setEntity(destination.getEntity());
                        destination.setEntity(temp);
                        */
                        _playerTile = destination;
                }

                //Check if destination is a wall
                else if(destination.getChar() == "█"){
                        System.out.println("You cannot move there");
                        return;
                }

                //do stuff if destination has an entity
                else{
                        //ATTACK HERE <NOT DONE>
                        attack(origin, destination);
                        return; //PROBABLY CHANGE
                }
        }

        public void attack(Tile attacker, Tile receiver){
                int aRow = attacker.getRow();
                int aCol = attacker.getCol();
                int rRow = receiver.getRow();
                int rCol = receiver.getCol();
                System.out.println("attacker damage :" +attacker.getEntity().dealDamage());
                _room[rRow][rCol].getEntity().takeDamage(attacker.getEntity().dealDamage());
                if (!_room[rRow][rCol].getEntity().isDead()){
                        _room[aRow][aCol].getEntity().takeDamage(attacker.getEntity().dealDamage());
                        checkDeath(attacker);
                }
                else{
                        checkDeath(receiver);
                }

        }

        public boolean checkDeath(Tile origin){
                if ((origin.getEntity() == getPlayer()) && (getPlayer().isDead())){
                        int pRow = _playerTile.getRow();
                        int pCol = _playerTile.getCol();
                        _room[pRow][pCol].setEntity(null);
                        System.out.println("RIP YOU DEADED");
                        System.out.println("GAME OVER");
                        throw new BindingOfRugException("RIPERINO IN PEPPERONI GAEM OVAR");
                }
                else if(origin.getEntity().isDead()){
                        Tile deadTemp = _room[origin.getRow()][origin.getCol()];
                        _room[origin.getRow()][origin.getCol()].setEntity(null);
                        ArrayQueue<Tile> holder = new ArrayQueue<Tile>(_monsterTileQueue.size());
                        int i = 0;
                        while(!(_monsterTileQueue.size() == 0)){
                                System.out.println("size " + _monsterTileQueue.size());
                                System.out.println("isEMpty: " + _monsterTileQueue.empty());
                                System.out.println("queuelength: " + _monsterTileQueue.queuelength());
                                Tile temp = _monsterTileQueue.dequeue();
                                if(temp != deadTemp)
                                        holder.enqueue(temp);
                                i++;

                        }
                        _monsterTileQueue = holder;
                        return true;
                }
                return false;
        }
        //asks the Monster to move

        public boolean checkMonsterClear(){
                //System.out.println(_monsterTileQueue.size());
                return (_monsterTileQueue.size() == 0);
        }

        public void askMonsterMove(){
                //NOT DONE
        }

        public ArrayQueue<Monster> getMonsters(){
                return _monsterQueue;
        }
        //Comparable
        public int compareTo(Object otherObject){
                Room otherRoom = (Room)otherObject;
                if(_monsterQueue.size() < otherRoom.getMonsters().size())
                return -1;
                else if(_monsterQueue.size() == otherRoom.getMonsters().size())
                return 0;
                return 1;
        }

}
