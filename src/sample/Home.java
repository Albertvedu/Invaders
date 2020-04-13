package sample;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Home implements Initializable {
    private int SPACE = 40;
    private int score = 0 ;
    private int cantidadNaves = 10;
    private int limiteDiparos = 14;
    private int totalEnemics = 30;
    private int posLimitDisparo = 230;
    private int posicionUltimoDisparo = 1150;
    private int posXprimeraNau = 180;
    private int posYprimeraNau = 165;
    private int incrementPosNauX = 140;
    private int incrementPosNauY = 140;
    private int moviment = 15;
    private int limitDretScreen = 320;
    private boolean misilRetarder = true;
    private ArrayList<Sprite> marcianoNaves;
    ArrayList<Sprite> misil = new ArrayList<>();
    private Scene scene;
    private GraphicsContext gc;
    private Player player;
    private SoundEffect  explosionEffect;


    private String s = getClass().getClassLoader().getResource("sound/soexplosio.wav").toExternalForm();
    private String disparo = getClass().getClassLoader().getResource("sound/disparo2.mp3").toExternalForm();

    Media sound = new Media(s);
    Media laser = new Media(disparo);
    Image imageFondo;
    @FXML
    ImageView fondoPantalla;
    @FXML
    AnchorPane anchorPaneFondo;
    @FXML Canvas canvas;

    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.217), new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent event) {
            gc.drawImage(imageFondo,0,0);
            gc.fillText("SCORE<1> "+ score +"\t\t\t\t\t\t    HI-SCORE\t\t\t\t\t\t\t\t  SCORE<2>", 30, 30);
            player.render(gc);
            detectarTeclado();
            renderitzarMarciano();
            renderitzarMisil();
            colisiones();
        }
    })
    );

    private void colisiones() {
        Iterator<Sprite> impactesIter = marcianoNaves.iterator();
        while (impactesIter.hasNext()){
            Sprite marciano = impactesIter.next();
            Iterator<Sprite> misilLanzado = misil.iterator();
            while (misilLanzado.hasNext()){
                Sprite proyectil = misilLanzado.next();
                if(proyectil.intersects(marciano)){
                    try {
                        impactesIter.remove();
                        misilLanzado.remove();
                        explosion(marciano.getPosX(), marciano.getPosY());
                        totalEnemics--;
                        score += 10;
                    }catch (Exception e){
                        System.out.println("errorrrr  en colisiones");
                    }
                    // subir score
                }else {
                    if (proyectil.getPosY() < posLimitDisparo) misilLanzado.remove();
                }
            }
        }
    }


    private void explosion(double x, double y){
        Sprite sprite = new Sprite();
        sprite.setImage(new Image("images/explosionn.png",105,105,false,false));
        sprite.setPosition(x-20,y-30);
        sprite.render(gc);
        MediaPlayer audioClip = new MediaPlayer(sound);
        audioClip.volumeProperty();
        audioClip.play();
    }

    public void renderitzarMarciano() {
        for ( Sprite nau: marcianoNaves){
            //  nau.clear(gc, nau.getPosX(), nau.getPosY());
            nau.setPosition(nau.moveX(), nau.moveY());
            nau.render(gc);
        }
        //    for ( Sprite nau: marcianoNaves){
        // nau.clear(gc, nau.getPosX(), nau.getPosY());
//            if (nau.getPosX() >= 1500){
//                int as = 0;
//                boolean ds = false;
//                for (Sprite a: marcianoNaves) {
////                    if (a.getId_nave() == 0) {
////                        System.out.println("get-90  : " + (a.getPosX()-90)+ "  " + a.getPosX()+ "  y: "+a.getPosY());
////                        a.setPosition(a.getPosX(, a.getPosY()+60);
////                        a.setDirX((-1) * a.getDirX());
////                    }else
//                    a.setDirX((-1) * a.getDirX());
//                    a.setPosY( a.getPosY()+60);
//
//                }
//                break;
//            }
//            nau.setPosX(nau.moveX(marcianoNaves, nau.getId_nave()));
//            nau.render(gc);
//        }
    }
    private void renderitzarMisil() {
        int pos = misil.size()-1;

        for (int i = 0; i < misil.size() ; i++) {
            misil.get(i).setPosition( misil.get(i).getPosX(), misil.get(i).move());
            if ( misil.get(pos).getPosY() < posicionUltimoDisparo){
                misilRetarder = true;
            }
            misil.get(i).render(gc);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anchorPaneFondo.setPrefHeight(Mides.APP_HEIGHT);;
        anchorPaneFondo.setPrefWidth(Mides.APP_WIDTH);
        canvas.setWidth(Mides.APP_WIDTH);
        canvas.setHeight(Mides.APP_HEIGHT);

        imageFondo = new Image("images/fondo.jpg", Mides.APP_WIDTH, Mides.APP_HEIGHT,false, false);
        fondoPantalla = new ImageView(imageFondo);
        player = new Player(new Image("images/nau.png", 120,60,false,false));
        crearMarciano();

        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("OCR A Std", 30));
        gc.fillRect(100,100,100,100);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    void detectarTeclado(){
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT){
                    player.moveRight();
                }
                if (event.getCode() == KeyCode.LEFT){
                    player.moveLeft();
                }
                if (misil.size() < limiteDiparos) {
                    if (event.getCode() == KeyCode.SPACE) {
                        disparar();
                    }
                }
            }
        });
    }
    public void setScene(Scene sc) {
        scene = sc;
    }

    public void crearMarciano(){
        marcianoNaves = new ArrayList<>();
        String valorParaId;
        int id = 0;
        for (int y = 165, i= 0 ; y < 450 && i < 3 ; y += 120, i++) {
            for (int x = 180, e = 0; x < 1700 && e < cantidadNaves; x += 140, e++) {
                valorParaId = String.valueOf(i)+String.valueOf(e);
                id = Integer.parseInt(valorParaId);
                Sprite marciano = new Sprite();
                marciano.setImage(new Image("images/ufo.png",75,35,false,false));
                marciano.setPosition(x,y);
                marciano.setId_nave(id);
                marcianoNaves.add(marciano);
                totalEnemics++;
            }
        }
    }
    public void disparar() {
        Sprite disparos = new Sprite();
        disparos.setImage(new Image("images/projectil.png", 15,15,false, false));
        disparos.setPosition(player.getPosX() + 50, player.getPosY() +20 );

        if ( misilRetarder ) {
            misil.add(disparos);
            misilRetarder = false;
            MediaPlayer laserClip = new MediaPlayer(laser);
            laserClip.play();
        }

    }
}