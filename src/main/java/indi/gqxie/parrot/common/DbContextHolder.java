package indi.gqxie.parrot.common;

public class DbContextHolder
{
    private static final ThreadLocal contextHolder = new ThreadLocal<>();

    public static void setDbType(DBTypeEnum dbTypeEnum)
    {
        contextHolder.set(dbTypeEnum.getValue());
    }

    public static String getDbType()
    {
        return (String) contextHolder.get();
    }

    public static void clearDbType()
    {
        contextHolder.remove();
    }
}
