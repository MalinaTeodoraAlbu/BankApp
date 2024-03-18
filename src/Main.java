import DataAccess.UserRepository;
import Models.*;

import javax.sound.midi.Soundbank;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


public class Main {
    static Scanner scanner = new Scanner(System.in);
    private static int optiune = -1;
    private static boolean inMeniu = true;
    private static boolean inMeniuCont = true;
    private static boolean inMeniuTranzictii = true;
    private static boolean islogging = true;
    static UserRepository userRepository = new UserRepository();

    public static void main(String[] args) {

        List<User> users = userRepository.readAll();

        while (islogging){
            System.out.println("LOGIN");
            System.out.println("CNP :");
            String cnp = scanner.nextLine();
            System.out.println("parola :");
            String parola = scanner.nextLine();

            boolean userFound = false;

            for (User user : users) {
                if (user.getCNP().equals(cnp)) {
                    userFound = true;
                    islogging = false;
                    if (user.getPassword().equals(parola)) {
                        System.out.println("Te-ai conectat cu succes!");
                        System.out.println("Bine ai venit, " + user.getNume() + "!");

                        while (inMeniu){
                            afiseazaMeniu();
                            citesteOptiunea();

                            switch (optiune){
                                case 1-> {
                                    System.out.println(user);
                                    System.out.println("Alege o alta optiune :");
                                    inMeniu = true;
                                }
                                case 2-> {
                                    System.out.println("Vă rugăm să alegeți un cont: ");
                                    for(int i=0; i< user.getConturiBancare().length;i++){
                                        System.out.println(i + 1 + ". " + user.getConturiBancare()[i].getTipContBancar() + " sold: " +
                                                user.getConturiBancare()[i].getSoldCont());
                                    }
                                    citesteOptiunea();
                                    if (optiune >= 0 && optiune <= user.getConturiBancare().length) {
                                        ContBancar contAles = user.getConturiBancare()[optiune - 1];

                                        System.out.println(contAles.getTipContBancar());
                                        System.out.println("SOLD: " + contAles.getSoldCont());
                                        inMeniuCont=true;
                                        while (inMeniuCont){
                                            menuCont();
                                            citesteOptiunea();
                                            switch (optiune){
                                                case 1-> System.out.println(contAles);
                                                case 2-> {
                                                    System.out.println(contAles);
                                                    contAles.afiseazaTranzactii();
                                                    inMeniuTranzictii = true;
                                                    while (inMeniuTranzictii) {
                                                        menuTranzictie();
                                                        inMeniuCont = false;
                                                        inMeniu=false;
                                                        citesteOptiunea();
                                                        switch (optiune) {
                                                            case 1 -> {
                                                                inMeniuCont = false;
                                                                inMeniu = false;
                                                                inMeniuTranzictii = true;
                                                                System.out.println("Despre care tranzactie vrei sa stii mai multe detalii?");
                                                                citesteOptiunea();
                                                                if (optiune >= 0 && optiune < contAles.getTranzactiiIstoric().size()) {
                                                                    System.out.println(contAles.getTranzactie(optiune - 1));
                                                                }

                                                            }
                                                            case 2 -> templateTranzactie(contAles);
                                                            case 0 -> {
                                                                inMeniuCont = true;
                                                                inMeniu = false;
                                                                inMeniuTranzictii = false;
                                                            }
                                                        }
                                                    }


                                                }
                                                case 3->  createTranzictie(user);
                                                case 4 -> adgNouCont(user);
                                                case 5 ->{
                                                    System.out.println("De pe care cont vrei sa transferi?");
                                                    int indexCont1 =  scanner.nextInt();
                                                    System.out.println("Pe care cont vrei sa transferi?");
                                                    int indexCont2 =  scanner.nextInt();
                                                    System.out.println("Ce suma doresti?");
                                                    float suma = citesteSumaValida();
                                                    transferaBaniIntreConturi(user.getConturiBancare()[indexCont1-1],user.getConturiBancare()[indexCont2-1],suma);


                                                }
                                                case 0-> {inMeniu = true;inMeniuCont=false;}
                                            }
                                        }
                                    } else {
                                        System.out.println("Opțiunea nu este validă. Te rugăm să alegi un număr valid.");
                                    }
                                }
                                case 3 -> raportCheltuieli(user);
                                case 0->iesire(user);

                            }
                        }

                        inMeniu = true;
                        optiune = -1;
                    } else {
                        System.out.println("Parola nu este corectă!");
                        islogging= true;
                    }
                }
            }

            if (!userFound) {
                System.out.println("CNP-ul introdus nu corespunde niciunui utilizator.");
                islogging=true;
            }

        }

    }


    public static void iesire(User user) {
        userRepository.update(user);
        System.exit(0);
    }


    public static void templateTranzactie(ContBancar contBancar){
        inMeniuCont = false;
        inMeniu = false;
        inMeniuTranzictii = true;
        System.out.println("Ce tranzactie vrei sa folosesti ca template? ");
        citesteOptiunea();
        System.out.println("Tranzactia aleasa " + contBancar.getTranzactie(optiune - 1));
        if (optiune >= 0 && optiune < contBancar.getTranzactiiIstoric().size()) {
            Tranzactie tranzactie = new Tranzactie();
            tranzactie.setTipTranzactie(contBancar.getTranzactie(optiune - 1).getTipTranzactie());
            tranzactie.setSumaTranzactie(contBancar.getTranzactie(optiune - 1).getSumaTranzactie());
            tranzactie.setDescrie(contBancar.getTranzactie(optiune - 1).getDescrie());
            tranzactie.setIBANDestinatar(contBancar.getTranzactie(optiune - 1).getIBANDestinatar());
            tranzactie.setIBANExpeditor(contBancar.getIBAN());
            tranzactie.setDataTranzactie(new Date());
            tranzactie.setUserId(contBancar.getTranzactie(optiune - 1).getUserId());
            contBancar.addTranzactie(tranzactie);
            System.out.println(tranzactie);
        }


    }

    public static void raportCheltuieli(User user) {
        for (ContBancar cont : user.getConturiBancare()) {
            List<Tranzactie> tranzactii = cont.getTranzactiiIstoric();

            LocalDate dataCurenta = LocalDate.now();
            LocalDate dataLunaAnterioara = dataCurenta.minusMonths(1);

            double sumaCheltuieliLunaAnterioara = 0.0;
            double sumaCheltuieliLunaCurenta = 0.0;

            for (Tranzactie tranzactie : tranzactii) {
                LocalDate dataTranzactie = tranzactie.getDataTranzactie().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

                if (tranzactie.getTipTranzactie().equals(TipTranzactie.RETRAGERE) || tranzactie.getTipTranzactie().equals(TipTranzactie.TRANSFER)) {
                    if (dataTranzactie.getMonthValue() == dataCurenta.getMonthValue() &&
                            dataTranzactie.getYear() == dataCurenta.getYear()) {
                        sumaCheltuieliLunaCurenta += tranzactie.getSumaTranzactie();
                    } else if (dataTranzactie.getMonthValue() == dataLunaAnterioara.getMonthValue() &&
                            dataTranzactie.getYear() == dataLunaAnterioara.getYear()) {
                        sumaCheltuieliLunaAnterioara += tranzactie.getSumaTranzactie();
                    }
                }
            }

            double diferenta = 0.0;
            if (sumaCheltuieliLunaAnterioara != 0) {
                diferenta = sumaCheltuieliLunaCurenta - sumaCheltuieliLunaAnterioara;
            }

            System.out.println("Raport pentru contul " + cont.getSoldCont());
            System.out.println("Suma cheltuielilor luna anterioară: " + sumaCheltuieliLunaAnterioara);
            System.out.println("Suma cheltuielilor luna curentă: " + sumaCheltuieliLunaCurenta);
            System.out.println("Diferența cheltuielilor față de luna anterioară: " + diferenta);
            System.out.println();
        }
    }


    public static void afiseazaMeniu() {
        System.out.println("---------------------------------------------------------------");
        System.out.println("Menu principal:");
        System.out.println("1. Vezi detalii user");
        System.out.println("2. Vezi conturi bancare");
        System.out.println("3. Vezi raport cheltuieli");
        System.out.println("0. Ieșire");
    }



    public static void menuCont(){
        System.out.println("---------------------------------------------------------------");
        System.out.println("Optiuni cont bancar:");
        System.out.println("1. Vezi detalii cont bancar");
        System.out.println("2. Vezi tranzactii");
        System.out.println("3. Creeaza tranzactie noua");
        System.out.println("4. Adauga cont de economii");
        System.out.println("5. Transfera bani catre alt cont pe care il deti");
        System.out.println("0. Inapoi");
    }


    public static void transferaBaniIntreConturi(ContBancar contBancar1, ContBancar contBancar2, float suma){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        if(suma > contBancar1.getSoldCont()){
            System.out.println("Nu se poate realiza transferul!");
        }
        else {
            Tranzactie tranzactie = new Tranzactie(currentDate,suma,TipTranzactie.TRANSFER,"Transfer catre conturi propii",contBancar2.getIBAN(),contBancar1.getUserID(),contBancar1.getIBAN());
            contBancar1.addTranzactie(tranzactie);
            System.out.println(tranzactie);
            System.out.println(contBancar1.getTipContBancar() + " sold :" + contBancar1.getSoldCont());
            System.out.println(contBancar2.getTipContBancar() + " sold :" + contBancar2.getSoldCont());
        }

    }


    public static void adgNouCont(User user){
        ContBancar contBancar = new ContBancar();
        contBancar.setSoldCont(0);
        contBancar.setUserID(user.getTokenUser());
        contBancar.setTipContBancar(TipContBancar.ECONOMII);
        System.out.println(contBancar);
        user.adaugaNouContBancar(contBancar);
    }


    public static void menuTranzictie(){
        System.out.println("---------------------------------------------------------------");
        System.out.println("Optiuni tranzactii: ");
        System.out.println("1. Vezi mai multe detalii despre o anumita tranzactie");
        System.out.println("2. Realizeaza o noua tranzactie pe baza uneia veche");
        System.out.println("0. Inapoi");
    }


    public static void createTranzictie(User user) {
        Scanner scanner = new Scanner(System.in);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());

        System.out.println("Current Date: " + currentDate);
        System.out.println("Suma :");
        System.out.print("> ");
        float suma = Float.parseFloat(scanner.nextLine());

        TipTranzactie tipTranzactie = null;
        System.out.println("Alege tip: ");
        System.out.println("1. DEPOZIT");
        System.out.println("2. RETRAGERE");
        System.out.println("3. TRANSFER");
        citesteOptiunea();
        switch (optiune) {
            case 1:
                tipTranzactie = TipTranzactie.DEPOZIT;
                break;
            case 2:
                tipTranzactie = TipTranzactie.RETRAGERE;
                break;
            case 3:
                tipTranzactie = TipTranzactie.TRANSFER;
                break;
            default:
                System.out.println("Invalid choice");
        }

        System.out.println("Descriere: ");
        String descriere = scanner.nextLine();

        System.out.println("In care cont bancar vreti sa adaugati noua tranzactie? ");
        for(int i=0; i< user.getConturiBancare().length;i++){
            System.out.println(i + 1 + ". " + user.getConturiBancare()[i].getTipContBancar() + " sold: " +
                    user.getConturiBancare()[i].getSoldCont());
        }
        citesteOptiunea();
        System.out.println("Contul in care se adauga " + optiune);

        String IBAN = "";
        System.out.println("IBAN destinatar: ");
        if(tipTranzactie.equals(TipTranzactie.DEPOZIT)){
            IBAN = user.getConturiBancare()[optiune - 1].getIBAN();
        } else if(tipTranzactie.equals(TipTranzactie.RETRAGERE)){
            IBAN = "-";
        }
        else{
            System.out.println("Te rog introduce IBAN-ul persoanei catre care vrei sa transferi bani");
            IBAN = scanner.nextLine();
        }
        System.out.println(IBAN);

        Tranzactie tranzactie = new Tranzactie(currentDate,suma,tipTranzactie,descriere,IBAN,user.getTokenUser(),user.getConturiBancare()[optiune - 1].getIBAN());
        user.getConturiBancare()[optiune - 1].addTranzactie(tranzactie);

    }


    public  static void citesteOptiunea() {
        try {
            System.out.print("> ");
            optiune = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("EROARE: Opțiunea trebuie să fie un număr întreg!");

        }
    }


    public static boolean isValidSum(float suma) {
        if (Float.isNaN(suma) || Float.isInfinite(suma)) {
            System.out.println("Suma introdusă nu este un număr valid.");
            return false;
        }
        if (suma <= 0) {
            System.out.println("Suma introdusă trebuie să fie pozitivă.");
            return false;
        }
        return true;
    }


    public static float citesteSumaValida() {
        float suma = 0;
        boolean sumaValida = false;

        while (!sumaValida) {
            try {
                System.out.println("Introduceți suma: ");
                suma = scanner.nextFloat();

                if (isValidSum(suma)) {
                    sumaValida = true;
                } else {
                    System.out.println("Suma introdusă nu este validă. Te rugăm să introduci o sumă validă și pozitivă.");
                }
            } catch (InputMismatchException e) {
                System.err.println("EROARE: Te rugăm să introduci un număr valid.");
                scanner.next();
            }
        }

        return suma;
    }


}
