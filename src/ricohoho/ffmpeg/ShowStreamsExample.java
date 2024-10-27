package ricohoho.ffmpeg; 

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffprobe.*;
import com.github.kokorin.jaffree.ffprobe.data.FormatParser;
import com.github.kokorin.jaffree.ffprobe.data.JsonFormatParser;
import com.github.kokorin.jaffree.ffprobe.data.ProbeData;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;


public class ShowStreamsExample {

    private static final Logger LOGGER = LoggerFactory.getLogger( "ShowStreamsExample" );


    public static void main(String[] args) {
        LOGGER.info( "Hello World with SLF4J" );
        if (args.length != 1) {
            System.err.println("Exactly 1 argument expected: path to media file");
            System.exit(1);
        }
        String pathToVideo = args[0];
        pathToVideo = "D:\\DATA\\e.fassel\\IdeaProjects\\Demo-FFMPEG2-JAFREE\\Emancipation.2022.MULTi.TRUEFRENCH.1080p.10bit.WEBRip.6CH.x265.HEVC-NoTag.mkv";

        getInfo(pathToVideo);

        //Appel de test de la mehode créant je JSON
        List<Document> InfoFilmSstreams = (new StreamFilm(new File(pathToVideo))).getInformationsFile();
        System.out.println("InfoFilmSstreams : "+InfoFilmSstreams.toString());

    }

    public static void getInfo(String pathToVideo) {
        LOGGER.info( "Dans la methode  : getInfo" );
        FormatParser formatParser=new JsonFormatParser() ;
        //InputStream rotatedInput = ShowStreamsExample.class.getResourceAsStream("rotated.mp4");
        //java.nio.file.Path FFMPEG_BIN = getFfmpegBin();
        FFprobeResult result = null;

        result = FFprobe.atPath()
                .setShowStreams(true)
                //.setInput(rotatedInput)
                .setInput(pathToVideo)
                .setLogLevel(LogLevel.DEBUG)
                .setFormatParser(formatParser)
                .execute();


        if (result !=null) {
            System.out.println("result ! nulll");
            for (Stream stream : result.getStreams()) {
                //System.out.println("Strem=" + stream.toString());
                String sInfo = "";
                String codecType = stream.getCodecType().toString();
                System.out.println("---------------------------->"+codecType +"["+stream.getIndex() +"]");
                sInfo = "Stream #" + stream.getIndex() + "]"
                        + " Codec type: [" + stream.getCodecType() + "]"
                        + " Codec Name : [" + stream.getCodecName() + "]"
                        + " duration: " + stream.getDuration() + " seconds"
                        //+ " Codec Tag : [" + stream.getCodecTag()+"]" null
                        + " Codec Long Name : [" + stream.getCodecLongName() + "]";
                //+ " Codec Long Name : [" + stream.get+"]"
                ;
                if (codecType.equals("VIDEO")) {
                    sInfo = sInfo + "\r\n *Résolution  : [" + stream.getCodedWidth() + "x" + stream.getCodedHeight() + "]"
                            + " *image par seconde : [" + stream.getAvgFrameRate() + "]"//24000/1001
                            //+ " Codec ChromaLocation  : [" + stream.getChromaLocation()+"]"
                            + " BitRate : [" + stream.getBitRate() + "]" //Null
                            + " TimeBase : [" + stream.getTimeBase() + "]" //1/1000
                            + " Extradata : [" + stream.getExtradata() + "]" //Null
                            + " *Format des pixel : [" + stream.getPixFmt() + "]" //yuv420p10le
                            + " Profil : [" + stream.getProfile() + "]" //Main 10
                            + " Channel : [" + stream.getChannels() + "]" //Null
                            + " Frame : [" + stream.getNbFrames() + "]" //Null
                            + " *Image de ref : [" + stream.getRefs() + "]" //Null
                            + " *Ratio d’aspect : [" + stream.getDisplayAspectRatio() + "]" //Null
                            + " getRFrameRate : [" + stream.getRFrameRate() + "]" //Null
                            + " *Niveau : [" + stream.getLevel() + "]" //Null!
                            + " getIndex : [" + stream.getIndex() + "]" //Null!
                            + " Id : [" + stream.getId() + "]" //Null!
                            + " getMaxBitRate : [" + stream.getMaxBitRate() + "]" //Null!
                            + " getNbFrames : [" + stream.getNbFrames() + "]" //Null!
                            + " getSampleRate : [" + stream.getSampleRate() + "]" //Null!
                    ;
                } else if (codecType.equals("AUDIO") || codecType.equals("SUBTITLE") ) {
                    sInfo = sInfo
                            + "\r\n TimeBase : [" + stream.getTimeBase() + "]" //1/1000
                            + " Profil : [" + stream.getProfile() + "]" //Main 10
                            + " Chaine : [" + stream.getChannels() + "]" //6 chaines
                            + "*Taux d'echantillonage : [" + stream.getSampleRate() + "]" //48 000 Hz
                            + " Codec ChannelLayout : [" + stream.getChannelLayout() + "]" //5.1
                            + " Tag langauge : [" +stream.getTag("language") + "]" //
                            + " Tag title : [" +stream.getTag("title") + "]" //
                            + " Tag BPS : [" +stream.getTag("BPS") + "]" //
                            + " Tag DURATION : [" +stream.getTag("DURATION") + "]" //
                            + " Tag NUMBER_OF_FRAMES : [" +stream.getTag("NUMBER_OF_FRAMES") + "]" //


                    //+ " getProbeData : [" + stream.getSideDataList().listIterator()+"]" //Null
                    ;
                } else if (codecType.equals("xxx")) {
                    sInfo = sInfo;
                    //+ " TimeBase : [" + stream.getTimeBase() + "]" //1/1000

                }
                //===> Side Data
                /*
                List<SideData> lisSlide = stream.getSideDataList();
                System.out.println("getSideDataList");
                if (lisSlide != null) {
                    System.out.println("GO1");
                    for (SideData sideData : lisSlide) {
                        System.out.println(sideData.getValue("title"));
                        System.out.println("sideData.getString()=" + sideData.getSideDataType());
                        System.out.println("sideData.getDisplayMatrix()=" + sideData.getDisplayMatrix());
                        System.out.println("sideData.getRotation()=" + sideData.getRotation());
                    }
                } else {
                    System.out.println("getSideDataList==> null");
                }

                ProbeData probdata = stream.getProbeData();
                if (probdata != null) {
                    System.out.println("GO2");
                    System.out.println("title=" + probdata.getSubData("title"));
                }
                */

                System.out.println(sInfo);
            }
            /*
            System.out.println("getFrames");
            List<FrameSubtitle> _list = result.getFrames();
            if (_list != null) {
                for (FrameSubtitle elet : _list) {
                    elet.toString();
                }
            }
            System.out.println("getPacketsAndFrames");
            List<PacketFrameSubtitle> __list = result.getPacketsAndFrames();
            if (__list != null) {
                for (PacketFrameSubtitle elet : __list) {
                    elet.toString();
                }
            }
            */
            System.out.println("getChapters");
            List<Chapter> ___list = result.getChapters();
            if (___list != null) {
                for (Chapter elet : ___list) {
                    elet.toString();
                }
            }
        }

    }
}