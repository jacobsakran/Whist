package com.example.whist20;

public class Dealer extends Player {
    String DealerName;

    public Dealer() {
    }

    public Dealer(String Uid, String userName, String DealerName) {
        super(Uid, userName);
        this.DealerName = DealerName;
    }

    @Override
    public String getName() {
        return this.DealerName;
    }
}
