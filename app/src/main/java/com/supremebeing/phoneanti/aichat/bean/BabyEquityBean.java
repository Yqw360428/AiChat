package com.supremebeing.phoneanti.aichat.bean;

import java.util.List;

public class BabyEquityBean extends BaseBean {
    public Data data;

    public static class Data{

        @Override
        public String toString() {
            return "BabyEquityBean{" +
                    "valid_count=" + valid_count +
                    ", valid_used_count=" + valid_used_count +
                    ", rights=" + rights +
                    '}';
        }

        private int valid_count;
        private int valid_used_count;
        /**
         * id : 12
         * right : {"code":"first_token","desc":{"gid":1,"price":"0.99","token_code":"com.zhibonvhai.livevideochat.day.0","token_amount":350,"original_price":"2.99","original_token_amount":350}}
         * available_at : 1635503647
         * expired_at : 1635503647
         */

        private List<BabyEquityBean.Data.Rights> rights;

        public int getValid_count() {
            return valid_count;
        }

        public void setValid_count(int valid_count) {
            this.valid_count = valid_count;
        }

        public int getValid_used_count() {
            return valid_used_count;
        }

        public void setValid_used_count(int valid_used_count) {
            this.valid_used_count = valid_used_count;
        }

        public List<BabyEquityBean.Data.Rights> getRights() {
            return rights;
        }

        public void setRights(List<BabyEquityBean.Data.Rights> rights) {
            this.rights = rights;
        }

        public static class Rights {
            private int id;
            /**
             * code : first_token
             * desc : {"gid":1,"price":"0.99","token_code":"com.zhibonvhai.livevideochat.day.0","token_amount":350,"original_price":"2.99","original_token_amount":350}
             */

            private BabyEquityBean.Data.Rights.Right right;
            private long available_at;
            private long expired_at;
            private boolean is_used;


            public boolean isIs_used() {
                return is_used;
            }

            public void setIs_used(boolean is_used) {
                this.is_used = is_used;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public BabyEquityBean.Data.Rights.Right getRight() {
                return right;
            }

            public void setRight(BabyEquityBean.Data.Rights.Right right) {
                this.right = right;
            }

            public long getAvailable_at() {
                return available_at;
            }

            public void setAvailable_at(long available_at) {
                this.available_at = available_at;
            }

            public long getExpired_at() {
                return expired_at;
            }

            public void setExpired_at(long expired_at) {
                this.expired_at = expired_at;
            }

            public static class Right {
                @Override
                public String toString() {
                    return "Right{" +
                            "code='" + code + '\'' +
                            ", desc=" + desc +
                            '}';
                }

                private String code;
                /**
                 * gid : 1
                 * price : 0.99
                 * token_code : com.zhibonvhai.livevideochat.day.0
                 * token_amount : 350
                 * original_price : 2.99
                 * original_token_amount : 350
                 */

                private BabyEquityBean.Data.Rights.Right.Desc desc;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public BabyEquityBean.Data.Rights.Right.Desc getDesc() {
                    return desc;
                }

                public void setDesc(BabyEquityBean.Data.Rights.Right.Desc desc) {
                    this.desc = desc;
                }

                public static class Desc {
                    @Override
                    public String toString() {
                        return "Desc{" +
                                "gid=" + gid +
                                ", price='" + price + '\'' +
                                ", token_code='" + token_code + '\'' +
                                ", token_amount=" + token_amount +
                                ", original_price='" + original_price + '\'' +
                                ", original_token_amount=" + original_token_amount +
                                ", next_day_token_amount=" + next_day_token_amount +
                                ", same_day_token_amount=" + same_day_token_amount +
                                '}';
                    }

                    private int gid;
                    private String price;
                    private String token_code;
                    private int token_amount;
                    private String original_price;
                    private int original_token_amount;
                    private int next_day_token_amount;
                    private int same_day_token_amount;

                    public int getNext_day_token_amount() {
                        return next_day_token_amount;
                    }

                    public void setNext_day_token_amount(int next_day_token_amount) {
                        this.next_day_token_amount = next_day_token_amount;
                    }

                    public int getSame_day_token_amount() {
                        return same_day_token_amount;
                    }

                    public void setSame_day_token_amount(int same_day_token_amount) {
                        this.same_day_token_amount = same_day_token_amount;
                    }

                    public int getGid() {
                        return gid;
                    }

                    public void setGid(int gid) {
                        this.gid = gid;
                    }

                    public String getPrice() {
                        return price;
                    }

                    public void setPrice(String price) {
                        this.price = price;
                    }

                    public String getToken_code() {
                        return token_code;
                    }

                    public void setToken_code(String token_code) {
                        this.token_code = token_code;
                    }

                    public int getToken_amount() {
                        return token_amount;
                    }

                    public void setToken_amount(int token_amount) {
                        this.token_amount = token_amount;
                    }

                    public String getOriginal_price() {
                        return original_price;
                    }

                    public void setOriginal_price(String original_price) {
                        this.original_price = original_price;
                    }

                    public int getOriginal_token_amount() {
                        return original_token_amount;
                    }

                    public void setOriginal_token_amount(int original_token_amount) {
                        this.original_token_amount = original_token_amount;
                    }
                }
            }

            @Override
            public String toString() {
                return "Rights{" +
                        "id=" + id +
                        ", right=" + right +
                        ", available_at=" + available_at +
                        ", expired_at=" + expired_at +
                        ", is_used=" + is_used +
                        '}';
            }
        }
    }

}
