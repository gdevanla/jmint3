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
                ISK,
                IPC,
                //
                PNC,
                PMD,
                //PMD,
                //PPD,
                PRV,
                //
                OMR,
                OMD,
                //OAN,
                //OAO,
                //JDC,
                //JSC,
                JTI,
                JTD,
                JID,
                EAM,
                //EMM,
                //EOA,
                //EOC

        };
    }
}
