package redshadus.vervarahgw;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CSVController
{
    private String filePath;

    public CSVController(String filePath)
    {
        this.filePath = filePath;
    }
    public void ConvertToCsv(List<HGPlayerClass> listOfTeams)
    {
        try
        {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath + ".csv", false)));

            for (HGPlayerClass item : listOfTeams)
            {
                System.out.println("inside the ConvertToCsv()  Try Block{}  Loop");
                String line = item.toString();

                pw.println(line);
                pw.flush();
            }
            pw.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public List<HGPlayerClass> convertFromCsv(String filePath)
    {
        List<HGPlayerClass> players = new ArrayList<HGPlayerClass>();
        Path pathToFile = Paths.get(filePath);
        try
        {
            BufferedReader br = new BufferedReader(Files.newBufferedReader(pathToFile));
            String line = br.readLine();

            while (line != null)
            {
                String[] lineCut = line.split(",");
                HGPlayerClass newPlayer = new HGPlayerClass(UUID.fromString(lineCut[0]), UUID.fromString(lineCut[1]), lineCut[2], Integer.parseInt(lineCut[3]), Boolean.getBoolean(lineCut[4]), Boolean.getBoolean(lineCut[5]));

                players.add(newPlayer);
                line = br.readLine();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return players;
    }
}