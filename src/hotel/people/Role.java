package hotel.people;

public class Role {
    private String name ,roleId;
    private int rank; 

    //constructor
    public Role(String name, String roleId, int rank){
        this.name = name;
        this.roleId = roleId;
        this.rank = rank;
    
    } 

    //get
    public String getName() {
        return name;
    }

    public String getRoleId() {
        return roleId;
    }

    public int getRank() {
        return rank;
    }

    //set
    public void setName(String name) {
        this.name = name;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }



}   
