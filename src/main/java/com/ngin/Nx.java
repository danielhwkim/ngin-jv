package com.ngin;

import java.io.IOException;
import java.util.Arrays;
import commander.Command.*;

interface IntStringFuction {
    void run(int id, String info);
}

interface VoidFuction {
    void run() throws IOException, InterruptedException;
}

public class Nx extends Ngin {
    //EventHandler eventHandler;

    public Nx() throws IOException {
        super();
    }

    protected void sendStageWait(NStageInfo.Builder builder) throws IOException, InterruptedException  {
        RemoteAction action = receiver.addRemoteAction();
        builder.setSn(action.sn);
        System.out.println(String.format("sendStageWait(%d)", action.sn));
        send(Head.stage, builder.build().toByteArray());
        action.lock();
    }

    protected void sendStage(NStageInfo.Builder builder) throws IOException  {
        send(Head.stage, builder.build().toByteArray());      
    }



    protected RemoteAction sendObjWait(NObject.Builder builder) throws IOException, InterruptedException {
        RemoteAction action = receiver.addRemoteAction();
        builder.setSn(action.sn);
        System.out.println(String.format("sendObjWait(%d)", action.sn));
        send(Head.object, builder.build().toByteArray());
        action.lock();
        return action;
    }

    protected void sendObj(NObject.Builder builder) throws IOException {
        send(Head.object, builder.build().toByteArray());
    }

    protected NStageInfo.Builder stageBuilder(float width, float height) {
        NStageInfo.Builder c = NStageInfo.newBuilder();
        c.setBackground("");
        c.setGravityX(0);
        c.setGravityY(0);
        c.setWidth(width);
        c.setHeight(height);
        c.setDebug(false);
        c.setJoystickDirectionals(JoystickDirectionals.none);
        c.setJoystickPrecision(3);
        c.setButton1(TouchMotion.DOWN);
        c.setButton2(TouchMotion.DOWN);
        return c;
    }

    protected NObject.Builder tilesBuilder(String data, float tileWidth, float tileHeight, float width, float height, Iterable<Integer> indices) {
        NObject.Builder c = NObject.newBuilder();
        c.setTid(0);
        NVisual.Builder v = c.getVisualBuilder();
        v.setPriority(0);
        v.setX(0);
        v.setY(0);
        v.setWidth(width);
        v.setHeight(height);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0);
        v.setAnchorY(0);
        NClip.Builder a = NClip.newBuilder();
        a.addAllIndices(indices);
        a.setData(data);
        a.setStepTime(0.5f);
        a.setWidth(tileWidth);
        a.setHeight(tileHeight);
        a.setType(NClipType.tiles);
        v.addClips(a);
        return c;
    }

    protected NObject.Builder objBuilder(int id, String info) {
        NObject.Builder c = NObject.newBuilder();
        c.setTid(0);
        c.setId(id);
        c.setInfo(info);
        return c;
    }

    protected NBody.Builder bodyBuilder(BodyShape shape, float x, float y) {
        NBody.Builder p = NBody.newBuilder();
        p.setX(x);
        p.setY(y);
        p.setWidth(1f);
        p.setHeight(1f);
        p.setRestitution(0);
        p.setFriction(0);
        p.setDensity(0);
        p.setAngle(0);
        p.setIsSensor(false);
        p.setCategoryBits(0x0001);
        p.setMaskBits(0xFFFF);
        p.setFixedRotation(true);
        p.setType(BodyType.dynamicBody);
        p.setTrackable(true);
        p.setContactReport(true);
        p.setPassableBottom(false);
        p.setShape(shape);
        return p;
    }
    
    protected NVisual.Builder visualBuilder(NClip.Builder[] builders, float x, float y) {
        NVisual.Builder v = NVisual.newBuilder();
        v.setPriority(0);
        v.setX(x);
        v.setY(y);
        v.setWidth(1);
        v.setHeight(1);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0.5f);
        v.setAnchorY(0.5f);
        for (int i=0; i<builders.length; i++) {
            v.addClips(builders[i]);
        }
        return v;
    }

    protected NVisual.Builder visualBuilder(NClip.Builder[] builders) {
        return visualBuilder(builders, 0, 0);
    }

    protected NVisual.Builder visualBuilder(Iterable<NClip> values, float x, float y) {
        NVisual.Builder v = NVisual.newBuilder();
        v.setPriority(0);
        v.setX(x);
        v.setY(y);
        v.setWidth(1);
        v.setHeight(1);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAnchorX(0.5f);
        v.setAnchorY(0.5f);
        v.addAllClips(values);
        return v;
    }

    protected NVisual.Builder visualBuilder(Iterable<NClip> builders) {
        return visualBuilder(builders, 0, 0);
    }    

    protected NClip.Builder clipBuilder(String data, float width, float height, Iterable<Integer> indices, NClipType type, float stepTime) {
        NClip.Builder a = NClip.newBuilder();
        if (indices != null) {
            a.addAllIndices(indices);
        }
        a.setData(data);
        a.setStepTime(stepTime);
        a.setX(0f);
        a.setY(0f);
        a.setWidth(width);
        a.setHeight(height);
        a.setType(type);
        return a;
    }

    protected NClip.Builder clipBuilder(String path, float width, float height, Iterable<Integer> indices) {
        return clipBuilder(path, width, height, indices, NClipType.loop, 0.05f);
    }

    protected NClip.Builder clipBuilder(String path, float width, float height) {
        return clipBuilder(path, width, height, null, NClipType.loop, 0.05f);
    }

    protected NClip.Builder clipBuilder(String path, float width, float height, float stepTime) {
        return clipBuilder(path, width, height, null, NClipType.loop, stepTime);
    }

    protected NClip.Builder clipBuilder(String path, float width, float height, NClipType type, float stepTime) {
        return clipBuilder(path, width, height, null, type, stepTime);
    }    

    /**
     * Runs the event loop for the provided event handler. The event loop will continuously poll the event queue for new events and pass them to the event handler to be handled. The event loop will continue to run until the event handler is completed.
     * 
     * @param handler The event handler to run the event loop for.
     * @throws IOException If an I/O error occurs while polling the event queue.
     * @throws InterruptedException If the thread is interrupted while polling the event queue.
    */

    public void runEventLoop(EventHandler handler) throws IOException, InterruptedException {
        //eventHandler = handler;
        while (!handler.completed) {
            handler.handle(queue.take());
        }
    }

    /**
     * Let the camera follow the object with the given ID.
     *
     * @param id The ID of the object to follow.
     * @throws IOException If an I/O error occurs while sending the command.
     */
    public void follow(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("follow");
        c.addInts(id);
        sendCmd(c);
    }

    /**
     * Removes the object with the given ID.
     *
     * @param id The ID of the object to remove.
     * @throws IOException If an I/O error occurs while sending the command.
     */    
    public void remove(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("remove");
        c.addInts(id);
        sendCmd(c);
    }
    
    /**
     * Submits the object with the given ID.
     *
     * @param id The ID of the object to submit.
     * @throws IOException If an I/O error occurs while sending the command.
     */    
    public void submit(int id) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("submit");
        c.addInts(id);
        c.addInts(4041);
        sendCmd(c);
    }

    /**
     * Gets the object info with the given ID.
     *
     * @param id The ID of the object to get info for.
     * @return The object info for the given ID.
     * @throws IOException If an I/O error occurs while sending the command.
     * @throws InterruptedException If the thread is interrupted while waiting for the response.
     */
    public NObjectInfo getObjInfo(int id) throws IOException, InterruptedException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("objinfo");
        c.addInts(id);
        RemoteAction action = sendCmdWait(c);
        return new NObjectInfo(action.event);
    }

    /**
     * Sets the clip index for the object with the given ID.
     *
     * @param id The ID of the object to set the clip index for.
     * @param index The index of the clip to set.
     * @param isFlipHorizonal Whether to flip the clip horizontally.
     * @throws IOException If an I/O error occurs while sending the command.
     */    
    public void setClipIndex(int id, int index, boolean isFlipHorizonal) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("clipIndex");
        c.addInts(id);
        c.addInts(index);
        c.addInts(isFlipHorizonal? 1:0);        
        sendCmd(c);
    }

    /**
     * Sets the clip index for the object with the given ID.
     *
     * @param id The ID of the object to set the clip index for.
     * @param index The index of the clip to set.
     * @throws IOException If an I/O error occurs while sending the command.
     */
    public void setClipIndex(int id, int index) throws IOException {
        setClipIndex(id, index, false);
    }

    /**
     * Plays the clip with the given index for the object with the given ID.
     * 
     * @param id The ID of the object to play the clip for.
     * @param index The index of the clip to play.
     * @param nextIndex The index of the next clip to play, or -1 if there is no next clip.
     * @param isFlipHorizonal Whether to flip the clip horizontally.
     * @param needAck Whether to wait for an acknowledgement from the server.
     * @throws IOException If an I/O error occurs while sending the command.
     * @throws InterruptedException If the thread is interrupted while waiting for the response.
     */

    public void playClip(int id, int index, int nextIndex, boolean isFlipHorizonal, boolean needAck) throws IOException, InterruptedException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("playClip");
        c.addInts(id);
        c.addInts(index);
        c.addInts(nextIndex);        
        c.addInts(isFlipHorizonal? 1:0);
        if (needAck) {
            sendCmdWait(c);
        } else {
            sendCmd(c);
        }
    }

    public void playClip(int id, int index, int nextIndex, boolean isFlipHorizonal) throws IOException, InterruptedException {
        playClip(id, index, nextIndex, isFlipHorizonal, false);
    }

    public void playClipWait(int id, int index, int nextIndex, boolean isFlipHorizonal) throws IOException, InterruptedException {
        playClip(id, index, nextIndex, isFlipHorizonal, true);
    }

    public void playClip(int id, int index, int nextIndex) throws IOException, InterruptedException {
        playClip(id, index, nextIndex, false, false);
    }

    public void playClipWait(int id, int index, int nextIndex) throws IOException, InterruptedException {
        playClip(id, index, nextIndex, false, true);
    }
    
    public void playClip(int id, int index) throws IOException, InterruptedException {
        playClip(id, index, index, false, false);
    }

    public void playClipWait(int id, int index) throws IOException, InterruptedException {
        playClip(id, index, index, false, true);
    }


    
    public NObjectInfo linearTo(int id, float x, float y, float speed) throws IOException, InterruptedException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linearTo");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);
        c.addFloats(speed);
        RemoteAction action = sendCmdWait(c);
        return new NObjectInfo(action.event);
    }

    public void forward(int id, float angle, float speed) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("forward");
        c.addInts(id);
        c.addFloats(angle);  
        c.addFloats(speed);                  
        sendCmd(c);
    }

    public void linear(int id, float x, float y) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linear");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);                     
        sendCmd(c);
    }

    public void force(int id, float x, float y) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("force");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);                     
        sendCmd(c);
    }

    public void impluse(int id, float x, float y) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("impluse");
        c.addInts(id);
        c.addFloats(x);
        c.addFloats(y);                     
        sendCmd(c);
    }    

    public void angular(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("angular");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }    

    public void torque(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("torque");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }    
    

    public void linearx(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("linearx");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }        


    public void lineary(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("lineary");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }  

    public void constx(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("constx");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }  

    public void consty(int id, float velocity) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("consty");
        c.addInts(id);
        c.addFloats(velocity);                  
        sendCmd(c);
    }      

    public void timer(int id, float time, String name) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("timer");
        c.addStrings(name);
        c.addInts(id);
        c.addFloats(time);
        sendCmd(c);
    }

    public void timer(float time, VoidFuction method) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("timer");
        c.addFloats(time);
        c.setSn(RemoteAction.getNewSn());
        EventHandler.addHandler(c.getSn(), method);
        sendCmd(c);
    }    
    
    public void audio(String cmd, String path) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audio");
        c.addStrings(cmd);
        c.addStrings(path);        
        sendCmd(c);
    }

    public void audioPlay(String path, float volume) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audio");
        c.addStrings("play");
        c.addStrings(path);
        c.addFloats(volume);        
        sendCmd(c);
    }

    public void audioLoop(String path, float volume) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audio");
        c.addStrings("loop");
        c.addStrings(path);
        c.addFloats(volume);        
        sendCmd(c);
    }    

    public void audioPool(String path, int maxPlayers) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audio");
        c.addStrings("pool");
        c.addStrings(path);
        c.addInts(maxPlayers);        
        sendCmd(c);
    }

    public void bgmPlay(String path, float volume) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("bgm");
        c.addStrings("play");
        c.addStrings(path);
        c.addFloats(volume);    
        sendCmd(c);
    }

    public void bgmStop() throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("bgm");
        c.addStrings("stop");
        sendCmd(c);
    }

    public void bgmPause() throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("bgm");
        c.addStrings("pause");
        sendCmd(c);
    }

    public void bgmResume() throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("bgm");
        c.addStrings("resume");
        sendCmd(c);
    }

    public void bgmVolume(float volume) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("bgm");
        c.addStrings("volume");
        c.addFloats(volume);    
        sendCmd(c);
    }

    public void audioCacheLoad(String[] pathes) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audioCache");
        c.addStrings("load");
        c.addAllStrings(Arrays.asList(pathes));
        sendCmd(c);
    }

    public void audioCacheClear() throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audioCache");
        c.addStrings("clear");
        sendCmd(c);
    }

    public void audioCacheClear(String[] pathes) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("audioCache");
        c.addStrings("clear");
        c.addAllStrings(Arrays.asList(pathes));        
        sendCmd(c);
    }

    public void distance(int id1, int id2) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("distance");
        c.addInts(id1);
        c.addInts(id2);        
        sendCmd(c);
    }

    public void syncClips(Iterable<Integer> ints) throws IOException {
        Cmd.Builder c = Cmd.newBuilder();
        c.addStrings("syncClips");
        c.addAllInts(ints);
        sendCmd(c);
    }

    enum AckType{
        NONE,
        ACK,
        EVENT,
        CALLBACK
    }
    class Transform {
        private boolean translating = false;
        private float tx;
        private float ty;
        private boolean scaling = false;
        private float sx;
        private float sy;
        private boolean rotating = false;
        private float a;
        private boolean opacity = false;
        private float o;
    
        public Transform translate(float x, float y) {
            this.tx = x;
            this.ty = y;
            this.translating = true;
            return this;
        }
    
        public Transform scale(float x, float y) {
            this.sx = x;
            this.sy = y;
            this.scaling = true;
            return this;
        }
    
        public Transform rotate(float a) {
            this.a = a;
            this.rotating = true;
            return this;
        }
    
        public Transform setOpacity(float o) {
          this.o = o;
          this.opacity = true;
          return this;
        }
    
        private Cmd.Builder builder(int id, float time, String type, AckType ackType) {
            Cmd.Builder c = Cmd.newBuilder();
            c.addStrings("transform");
            c.addStrings(type);
            c.addInts(id);
            switch(ackType) {
                case NONE:
                c.addInts(0);
                break;
                case ACK:
                c.addInts(1);
                break;                
                case EVENT:
                c.addInts(2);
                break;                
                case CALLBACK:
                c.addInts(3);
                break;
            }

            c.addFloats(time);
        
            if (this.translating) {
              c.addInts(1);
              c.addFloats(this.tx);
              c.addFloats(this.ty);
            } else {
              c.addInts(0);
              c.addFloats(0);
              c.addFloats(0);
            }
        
            if (this.scaling) {
              c.addInts(1);
              c.addFloats(this.sx);
              c.addFloats(this.sy);
            } else {
              c.addInts(0);
              c.addFloats(0);
              c.addFloats(0);
            }
        
            if (this.rotating) {
              c.addInts(1);
              c.addFloats(this.a);
            } else {
              c.addInts(0);
              c.addFloats(0);
            }
    
            if (this.opacity) {
              c.addInts(1);
              c.addFloats(this.o);
            } else {
              c.addInts(0);
              c.addFloats(0);
            }
        
            return c;        
        }
    
        Cmd.Builder builder(int id, float time) {
            return builder(id, time, "easeInOut", AckType.NONE);
        }

        public void send(int id, float time, String type) throws IOException {
            Cmd.Builder c = builder(id, time, type, AckType.NONE);
            sendCmd(c);
        }

        public void send(int id, float time, String type, VoidFuction method) throws IOException {
            Cmd.Builder c = builder(id, time, type, AckType.CALLBACK);
            c.setSn(RemoteAction.getNewSn());
            EventHandler.addHandler(c.getSn(), method);
            sendCmd(c);
        }        
        
        public void sendWithAck(int id, float time, String type) throws IOException, InterruptedException {
            Cmd.Builder c = builder(id, time, type, AckType.ACK);
            sendCmdWait(c);
        }
    
        public void sendWithEvent(int id, float time, String type) throws IOException, InterruptedException {
            Cmd.Builder c = builder(id, time, type, AckType.EVENT);
            sendCmdWait(c);
        }      
    
        public void send(int id, float time) throws IOException {
            Cmd.Builder c = builder(id, time, "easeInOut", AckType.NONE);
            sendCmd(c);
        }

        public void send(int id, float time, VoidFuction method) throws IOException {
            Cmd.Builder c = builder(id, time, "easeInOut", AckType.CALLBACK);
            c.setSn(RemoteAction.getNewSn());
            EventHandler.addHandler(c.getSn(), method);            
            sendCmd(c);
        }        
        
        public void sendWithAck(int id, float time) throws IOException, InterruptedException {
            Cmd.Builder c = builder(id, time, "easeInOut", AckType.ACK);
            sendCmdWait(c);
        }
    
        public void sendWithEvent(int id, float time) throws IOException {
            Cmd.Builder c = builder(id, time, "easeInOut", AckType.EVENT);
            sendCmd(c);
        }

    
        public void send(int id) throws IOException {
            Cmd.Builder c = builder(id, 0, "easeInOut", AckType.NONE);
            sendCmd(c);
        }

        public void send(int id, VoidFuction method) throws IOException {
            Cmd.Builder c = builder(id, 0, "easeInOut", AckType.CALLBACK);
            c.setSn(RemoteAction.getNewSn());
            EventHandler.addHandler(c.getSn(), method);            
            sendCmd(c);
        }        
        
        public void sendWithAck(int id) throws IOException, InterruptedException {
            Cmd.Builder c = builder(id, 0, "easeInOut", AckType.ACK);
            sendCmdWait(c);
        }
    
        public void sendWithEvent(int id) throws IOException {
            Cmd.Builder c = builder(id, 0, "easeInOut", AckType.EVENT);
            sendCmd(c);
        }
    }

    class Stage {
        private NStageInfo.Builder builder;
        Stage(float width, float height) {
            builder = stageBuilder(width, height);
        }

        Stage enableDebug() {
            builder.setDebug(true);
            return this;
        }

        Stage enableTap() {
            builder.setTap(TouchMotion.ALL);
            return this;
        }

        Stage enableDebug(boolean value) {
            builder.setDebug(value);
            return this;
        }   

        void sendWithAck() throws IOException, InterruptedException {
            sendStageWait(builder);
        }

        void send() throws IOException {
            sendStage(builder);
        }
    }

    static float defaultStepTime = 0.05f; 
    class Visible {
        public NVisual.Builder v;

        Visible(float x, float y) {
            v = NVisual.newBuilder();
            v.setPriority(0);
            v.setX(x);
            v.setY(y);
            v.setWidth(1);
            v.setHeight(1);
            v.setScaleX(1);
            v.setScaleY(1);
            v.setAnchorX(0.5f);
            v.setAnchorY(0.5f);
        }

        public Visible setSize(float width, float height) {
            v.setWidth(width);
            v.setHeight(height);
            return this;
        }

        public Visible setPriority(int value) {
            v.setPriority(value);
            return this;
        }        

        public Visible setPos(float x, float y) {
            v.setX(x);
            v.setY(y);
            return this;
        }

        public Visible setScale(float x, float y) {
            v.setScaleX(x);
            v.setScaleY(y);
            return this;
        }

        public Visible setAnchor(float x, float y) {
            v.setAnchorX(x);
            v.setAnchorY(y);
            return this;
        }

        public Visible setCurrent(int value) {
            v.setCurrent(value);
            return this;
        }

        public Visible addClip(String path, float width, float height, Iterable<Integer> indices) {
            v.addClips(clipBuilder(path, width, height, indices, NClipType.loop, defaultStepTime));
            return this;
        }
    
        public Visible addClip(String path, float width, float height) {
            v.addClips(clipBuilder(path, width, height, null, NClipType.loop, defaultStepTime));
            return this;            
        }
    
        public Visible addClip(String path, float width, float height, float stepTime) {
            v.addClips(clipBuilder(path, width, height, null, NClipType.loop, stepTime));
            return this;            
        }
    
        public Visible addClip(String path, float width, float height, NClipType type, float stepTime) {
            v.addClips(clipBuilder(path, width, height, null, type, stepTime));
            return this;            
        }        

        public Visible send(int id, String name) throws IOException {
            sendObj(objBuilder(id, name).setVisual(v));
            return this;
        }

        public Visible sendWithAck(int id, String name) throws IOException, InterruptedException {
            sendObjWait(objBuilder(id, name).setVisual(v));
            return this;            
        }
        
        public Visible send(int id) throws IOException {
            send(id, "");
            return this;            
        }

        public Visible sendWithAck(int id) throws IOException, InterruptedException {
            sendWithAck(id, "");
            return this;            
        }        
    }
}
