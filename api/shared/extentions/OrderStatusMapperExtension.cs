namespace api.shared.extentions;

public static class OrderStatusMapperExtension
{
    public static string ToOrderStatusName(this int orderStatus)
    {
        return orderStatus switch
        {
            0 => "Rejected",
            1 => "Inprogress",
            2 => "Accepted",
            3 => "In away",
            4 => "Received",
            _ => "Completed",
        };
    }
}