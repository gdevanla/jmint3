package jmint.mutants;

public enum MutantsCode {
    IHD,
    IHI,
    IOD,
    IOP,
    IOR,
    IPC,
    ISK,
    JDC,
    JID,
    JTD,
    JSC,
    OAN,
    OAO,
    OMD,
    OMR,
    PMD,
    PNC,
    PPD,
    PRV,
    EAM,
    EMM,
    EOA,
    JTI,
    EOC, ISD;

    public static MutantsCode[] getAllMutantCodes(){
        return new MutantsCode[]{
                IHD,
                IHI,
                IOD,
                //IOP,
                //IOR,
                ISK,   //non-static
                IPC,
                //
                PNC,
                PMD,
                //PMD,
                //PPD,
                PRV,    //non-static
                //
                OMR,    //non-static
                OMD,    //non-static
                //OAN,
                //OAO,
                //JDC,
                //JSC,
                JTI,   //non-static
                JTD,   //non-static
                JID,
                EAM,   //non-static
                //EMM,
                //EOA,
                //EOC

        };
    }
}
