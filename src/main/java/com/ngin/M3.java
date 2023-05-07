package com.ngin;
import java.io.IOException;
import java.util.*;

import com.ngin.Nx.Visible;

import commander.Command.*;
public class M3 extends EventHandler {
    Nx nx;
    Board board;
    Vec2 posOrigin;

    enum MoveType {
        IDLE,
        USER,
        AUTO
    }

    MoveType moveType = MoveType.IDLE;

    static final float EXCHANGE_SEC = 0.3f;
    static final float MOVEMENT_SEC = 0.2f;
    static final float OFFSET_X = 0.5f;
    static final float OFFSET_Y = 0.5f;

    Random rand = new Random();

    final String[] fruitNames = {"Bananas", "Pineapple", "Cherries", "Orange"}; // "Apple", "Melon", "Strawberry", "Kiwi"};

    void addFruit(Item item) throws IOException {
        Visible obj = nx.new Visible(0.5f + item.pos.x, 0.5f + item.pos.y);
        for (int i=0; i<fruitNames.length; i++) {
            obj.addClip("Items/Fruits/" + fruitNames[i] + ".png", 32f, 32f, 0.05f);
        }
        obj.addClip("Items/Fruits/Collected.png", 32f, 32f, NClipType.once, 0.05f).setCurrent(item.fruit).send(item.id, "fruit");   
    }

    class Item {
        int fruit;
        int id;
        Int2 pos;
        boolean removed = false;

        Item(Int2 p, int fruitId, int objId) {
            pos = p;
            fruit = fruitId;
            id = objId;
        }

        @Override
        public String toString() {
            return String.format("%s [%d] %s", fruitNames[fruit], id, pos.toString());
        }
    }

    class Board {
        private ArrayList<Integer> idList;
        private Item[][] items;

        int width;
        int height;

        Board(int w, int h) {
            width = w;
            height = h;
            items = new Item[width][height];
            idList = new ArrayList<>();
        }

        void buildIdList() {
            idList.clear();
            for (int i=0; i<height; i++) {
                for (int j=0; j<width; j++) {
                    Item item = get(j, i);
                    if (item != null) {
                        idList.add(item.id);
                    }
                }
            }
        }

        void syncClips() throws IOException {
            nx.syncClips(idList);
        }

        Item get(int x, int y) {
            return items[x][y];
        }

        Item get(Int2 pos) {
            return get(pos.x, pos.y);
        }

        Item remove(Int2 pos) {
            Item m = get(pos.x, pos.y);
            setNull(pos.x, pos.y);
            return m;
        }

        void set(Item item) {
            assert items[item.pos.x][item.pos.y] == null;
            items[item.pos.x][item.pos.y] = item;
        }

        void setNull(int x, int y) {
            items[x][y] = null;
        }

        
        void exchange(Int2 i1, Int2 i2) {
            Item item1 = remove(i1);
            Item item2 = remove(i2);
            item1.pos = i2;
            item2.pos = i1;
            set(item1);
            set(item2);
        }

        boolean inside(Int2 pos) {
            return pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            for (int i=0; i<height; i++) {
                for (int j=0; j<width; j++) {
                    Item m = get(j, i);
                    str.append((m != null)? fruitNames[m.fruit].charAt(0) : ' ');
                }
                str.append('\n');
            }

            return str.toString();
        }
    }



    class Int2 {
        int x;
        int y;
        Int2(int i0, int i1) {
            x = i0;
            y = i1;
        }

        Int2 add(Int2 int2) {
            return new Int2(x+int2.x, y+int2.y);
        }

        @Override
        public String toString() {
            return String.format("Int2(%d, %d)", x, y);
        }      
    }

    class Vec2 {
        float x;
        float y;

        Vec2(float f0, float f1) {
            x = f0;
            y = f1;
        }

        int xFloored() {
            return (int)Math.floor(x);
        }

        int yFloored() {
            return (int)Math.floor(y);
        }

        Int2 floor() {
            return new Int2(xFloored(), yFloored());
        }

        double distanceSquared(float x2, float y2) {
            double dx = x2 - x;
            double dy = y2 - y;
            return dx*dx + dy*dy;
        }

        @Override
        public String toString() {
            return String.format("[%f, %f]", x, y);
        }
    }

    void match() throws IOException, InterruptedException {
        int max;                                
        max = removeMatches();
        if (max > 0) {
            nx.timer(MOVEMENT_SEC*max, () -> {
                assert moveType == MoveType.AUTO;
                if (findMatches()) {
                    nx.timer(EXCHANGE_SEC,this::match);
                    nx.audioPlay("kenney_digitalaudio/powerUp7.mp3", 0.1f);                
                } else {
                    board.syncClips();
                    moveType = MoveType.IDLE;
                }
            });
        } else {
            moveType = MoveType.IDLE;
        }                                
    }


    void exchange(Int2 a, Int2 b) throws IOException {
        changes.clear();
        changes.addFirst(board.get(a));
        changes.addFirst(board.get(b));
        nx.new Transform().translate(a.x + 0.5f, a.y + 0.5f).send(board.get(b).id, EXCHANGE_SEC);
        nx.new Transform().translate(b.x + 0.5f, b.y + 0.5f).send(board.get(a).id, EXCHANGE_SEC, () -> {
            if (moveType == MoveType.AUTO) {
                Item aa = changes.get(0);
                Item bb = changes.get(1);
                if (aa.fruit == bb.fruit || !findMatches()) {
                    exchange(aa.pos, bb.pos);
                    moveType = MoveType.IDLE;
                } else {
                    nx.timer(EXCHANGE_SEC, this::match);
                    nx.audioPlay("kenney_digitalaudio/powerUp7.mp3", 0.1f);                        
                }
            }
        });
        board.exchange(a, b);
    }

    void move(Int2 d) throws IOException {
        Int2 origin = posOrigin.floor();
        Int2 other = origin.add(d);
        if (board.inside(other)) {
            exchange(origin, other);
        } else {
            nx.new Transform().translate(origin.x + 0.5f, origin.y + 0.5f).send(board.get(origin).id, EXCHANGE_SEC, () -> {
                if (moveType == MoveType.AUTO) {
                        moveType = MoveType.IDLE;
                }
            });
        }
    }

    int countFruits(Int2 ori, int fruit, Int2 d) {
        int count = 0;
        Int2 pos = ori.add(d);
        while(board.inside(pos) && board.get(pos) != null && board.get(pos).fruit == fruit) {
            count += 1;            
            pos = pos.add(d);
        }
        return count;
    }

    boolean checkFruits(Item item) throws IOException, InterruptedException {
        Int2 pos = item.pos;
        int fruit = item.fruit;

        int left = countFruits(pos, fruit, new Int2(-1, 0));
        int right = countFruits(pos, fruit, new Int2(1, 0));
        int up = countFruits(pos, fruit, new Int2(0, -1));
        int down = countFruits(pos, fruit, new Int2(0, 1));
        boolean match = false;

        System.out.println(String.format("%s left:%d, right:%d, up:%d, down:%d", item.toString(), left, right, up, down));

        if (left + right > 1) {
            for (int i = pos.x-left; i <= pos.x+right; i++) {
                Item m = board.get(i, pos.y);
                if (!m.removed) {
                    m.removed = true;
                    nx.playClip(m.id, fruitNames.length);
                    match = true;
                }

            }
        }

        if (up + down > 1) {
            for (int i = pos.y-up; i <= pos.y+down; i++) {
                Item m = board.get(pos.x, i);
                if (!m.removed) {
                    m.removed = true;
                    nx.playClip(m.id, fruitNames.length);
                    match = true;
                }

            }
        }
        return match;
    }

    int removeMatches() throws IOException, InterruptedException {

        LinkedList<Item> list = new LinkedList<>();
        int maxRemoved = 0;

        changes.clear();

        for (int i=0; i<board.width; i++) {
            int countRemoved = 0;
            for (int j=board.height-1; j>=0; j--) {
                Item m = board.get(i, j);
                if (m == null) continue;
                assert m != null;
                if (m.removed) {
                    board.setNull(m.pos.x, m.pos.y);
                    countRemoved += 1;
                    m.pos.y = -countRemoved;
                    nx.new Transform().translate(m.pos.x + 0.5f, -countRemoved + 0.5f).send(m.id);
                    list.add(m);
                } else if (countRemoved>0) {
                    board.setNull(m.pos.x, m.pos.y);
                    m.pos.y += countRemoved;
                    board.set(m);
                    changes.addFirst(m);
                    nx.new Transform().translate(m.pos.x + 0.5f, m.pos.y + 0.5f).send(m.id, MOVEMENT_SEC*countRemoved);
                }
            }

            while (!list.isEmpty()) {
                Item m = list.removeFirst();
                int fruit = rand.nextInt(fruitNames.length);

                m.fruit = fruit;
                m.removed = false;
                nx.playClip(m.id, fruit);
                m.pos.y += countRemoved;
                board.set(m);
                changes.addFirst(m);                
                nx.new Transform().translate(m.pos.x + 0.5f, m.pos.y + 0.5f).send(m.id, MOVEMENT_SEC*countRemoved);
            }

            if (countRemoved > maxRemoved) {
                maxRemoved = countRemoved;
            }
        }
        System.out.println(board);
        return maxRemoved;
    }

    LinkedList<Item> changes = new LinkedList<>();

    boolean findMatches() throws IOException, InterruptedException {
        boolean found = false;

        if (!changes.isEmpty()) {
            System.out.println(changes);

            for (Item item : changes) {
                if (checkFruits(item)) found = true;
            }
        }
        return found;
    }



    @Override
    public void onTap(TapInfo info) throws IOException, InterruptedException {
        if (info.motion == TouchMotion.DOWN && moveType == MoveType.IDLE) {
            posOrigin = new Vec2(info.x, info.y);
            if (board.inside(posOrigin.floor())) {
                moveType = MoveType.USER;
            }
        } else if (info.motion == TouchMotion.MOVE && moveType == MoveType.USER) {
            double dx = info.x - posOrigin.x;
            double dy = info.y - posOrigin.y;
            double dx2 = dx*dx;
            double dy2 = dy*dy;

            if (dx2 + dy2 < 0.5*0.5) {
                nx.new Transform().translate(info.x, info.y).send(board.get(posOrigin.floor()).id);
            } else {
                moveType = MoveType.AUTO;

                if (dx2 > dy2) {
                    if (dx>0) {
                        move(new Int2(1, 0));
                    } else {
                        move(new Int2(-1, 0));                        
                    }
                } else {
                    if (dy>0) {
                        move(new Int2(0, 1));                        
                    } else {
                        move(new Int2(0, -1));                        
                    }                    
                }
            }
        } else {
            if (moveType == MoveType.USER) {
                nx.new Transform().translate(posOrigin.xFloored() + 0.5f, posOrigin.yFloored() + 0.5f).send(board.get(posOrigin.floor()).id); 
                moveType = MoveType.IDLE;
            }      
        }
    }
    
    public void run() throws IOException, InterruptedException {
        nx = new Nx();
        float width = 5;
        float height = 10;

        nx.new Stage(width, height).enableTap().enableDebug().sendWithAck();

        board = new Board((int)width, (int)height);
        
        for (int x =0; x<width; x++) {
            for (int y = 0; y<height; y++) {
                int fruit = rand.nextInt(fruitNames.length);
                int id = x*100 + y;
                Item item = new Item(new Int2(x, y), fruit, id);
                board.set(item);
                addFruit(item);
            }
        }

        board.buildIdList();
        nx.runEventLoop(this);
    }    
}
