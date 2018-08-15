package indi.gqxie.parrot.common;

public enum DBTypeEnum
{
    PARROT("parrot"),
    TEST("test");
    private String value;

    DBTypeEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
