/**
 * Created by 李勇志 on 2016/12/6.
 * 2014301500370
 *
 * 本工程包含两个数据文件
 * fulldata为老师给的原始数据文件，由于数据量过大程序跑不出来结果，没有选用进行测试
 * top1000data是从fulldata中摘取的前1000条数据，本程序运行的结果是基于这前1000条数据进行的频繁项集挖掘和关联度分析
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Apriori算法实现 最大模式挖掘，涉及到支持度，但没有置信度计算
 *
 * AssociationRulesMining()函数实现置信度计算和关联规则挖掘
 */
public class AprioriMyself {

    public static  int times=0;//迭代次数
    private static  double MIN_SUPPROT = 0.02;//最小支持度百分比
    private static   double MIN_CONFIDENCE=0.6;//最小置信度
    private static boolean endTag = false;//循环状态，迭代标识
    static List<List<String>> record = new ArrayList<List<String>>();//数据集
    static  List<List<String>> frequentItemset=new ArrayList<>();//存储所有的频繁项集
    static List<Mymap> map = new ArrayList();//存放频繁项集和对应的支持度技术

    public static void main(String args[]){

        System.out.println("请输入最小支持度（如0.05）和最小置信度（如0.6）");
        Scanner in=new Scanner(System.in);
        MIN_SUPPROT=in.nextDouble();
        MIN_CONFIDENCE=in.nextDouble();


        /*************读取数据集**************/
        record = getRecord("top1000data");
        //控制台输出记录
        System.out.println("读取数据集record成功===================================");
        ShowData(record);


        Apriori();//调用Apriori算法获得频繁项集
        System.out.println("频繁模式挖掘完毕。\n\n\n\n\n进行关联度挖掘，最小支持度百分比为："+MIN_SUPPROT+"  最小置信度为："+MIN_CONFIDENCE);



         AssociationRulesMining();//挖掘关联规则
    }

    /**********************************************
     * ****************读取数据********************/
    public static List<List<String>> getRecord(String url) {
        List<List<String>> record = new ArrayList<List<String>>();
        try {
            String encoding = "UTF-8"; // 字符编码(可解决中文乱码问题 )
            File file = new File(url);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTXT = null;
                while ((lineTXT = bufferedReader.readLine()) != null) {//读一行文件
                    String[] lineString = lineTXT.split(",");
                    List<String> lineList = new ArrayList<String>();
                    for (int i = 0; i < lineString.length; i++) {
                        lineList.add(lineString[i]);
                    }
                    record.add(lineList);
                }

                read.close();
            } else {
                System.out.println("找不到指定的文件！");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容操作出错");
            e.printStackTrace();
        }
        return record;
    }




    public static void Apriori()           /**实现apriori算法**/
    {
        //************获取候选1项集**************
        System.out.println("第一次扫描后的1级 备选集CandidateItemset");
        List<List<String>> CandidateItemset = findFirstCandidate();
        ShowData(CandidateItemset);



        //************获取频繁1项集***************
        System.out.println("第一次扫描后的1级 频繁集FrequentItemset");
        List<List<String>> FrequentItemset = getSupprotedItemset(CandidateItemset);
        AddToFrequenceItem(FrequentItemset);//添加到所有的频繁项集中
        //控制台输出1项频繁集
        ShowData(FrequentItemset);


         //*****************************迭代过程**********************************
        times=2;
        while(endTag!=true){

            System.out.println("*******************************第"+times+"次扫描后备选集");
            //**********连接操作****获取候选times项集**************
            List<List<String>> nextCandidateItemset = getNextCandidate(FrequentItemset);
            //输出所有的候选项集
            ShowData(nextCandidateItemset);


            /**************计数操作***由候选k项集选择出频繁k项集****************/
            System.out.println("*******************************第"+times+"次扫描后频繁集");
            List<List<String>> nextFrequentItemset = getSupprotedItemset(nextCandidateItemset);
            AddToFrequenceItem(nextFrequentItemset);//添加到所有的频繁项集中
            //输出所有的频繁项集
            ShowData(nextFrequentItemset);


            //*********如果循环结束，输出最大模式**************
            if(endTag == true){
                System.out.println("\n\n\nApriori算法--->最大频繁集==================================");
                ShowData(FrequentItemset);
            }
            //****************下一次循环初值********************
            FrequentItemset = nextFrequentItemset;
            times++;//迭代次数加一
        }
    }



    public static void AssociationRulesMining()//关联规则挖掘
    {
        for(int i=0;i<frequentItemset.size();i++)
        {
            List<String> tem=frequentItemset.get(i);
            if(tem.size()>1) {
                List<String> temclone=new ArrayList<>(tem);
                List<List<String>> AllSubset = getSubSet(temclone);//得到频繁项集tem的所有子集
                for (int j = 0; j < AllSubset.size(); j++) {
                    List<String> s1 = AllSubset.get(j);
                    List<String> s2 = gets2set(tem, s1);
                    double conf = isAssociationRules(s1, s2, tem);
                    if (conf > 0)
                        System.out.println("置信度为：" + conf);
                }
            }

            }
        }


    public  static  double isAssociationRules(List<String> s1,List<String> s2,List<String> tem)//判断是否为关联规则
    {
        double confidence=0;
        int counts1;
        int countTem;
        if(s1.size()!=0&&s1!=null&&tem.size()!=0&&tem!=null)
        {
            counts1= getCount(s1);
            countTem=getCount(tem);
            confidence=countTem*1.0/counts1;

            if(confidence>=MIN_CONFIDENCE)
            {
                System.out.print("关联规则："+ s1.toString()+"=>>"+s2.toString()+"   ");
                return confidence;
            }
            else
                return 0;

        }
        else
            return 0;

    }

    public static int getCount(List<String> in)//根据频繁项集得到 其支持度计数
    {
        int rt=0;
        for(int i=0;i<map.size();i++)
        {
            Mymap tem=map.get(i);
            if(tem.isListEqual(in)) {
                rt = tem.getcount();
                return rt;
            }
        }
        return rt;

    }


    public static  List<String> gets2set(List<String> tem, List<String> s1)//计算tem减去s1后的集合即为s2
    {
        List<String> result=new ArrayList<>();

        for(int i=0;i<tem.size();i++)//去掉s1中的所有元素
        {
            String t=tem.get(i);
            if(!s1.contains(t))
                result.add(t);
        }
        return  result;
    }


    public static List<List<String>> getSubSet(List<String> set){
        List<List<String>> result = new ArrayList<>();	//用来存放子集的集合，如{{},{1},{2},{1,2}}
        int length = set.size();
        int num = length==0 ? 0 : 1<<(length);	//2的n次方，若集合set为空，num为0；若集合set有4个元素，那么num为16.

        //从0到2^n-1（[00...00]到[11...11]）
        for(int i = 1; i < num-1; i++){
            List<String> subSet = new ArrayList<>();

            int index = i;
            for(int j = 0; j < length; j++){
                if((index & 1) == 1){		//每次判断index最低位是否为1，为1则把集合set的第j个元素放到子集中
                    subSet.add(set.get(j));
                }
                index >>= 1;		//右移一位
            }

            result.add(subSet);		//把子集存储起来
        }
        return result;
    }





    public  static  boolean  AddToFrequenceItem(List<List<String>> fre)
    {

        for(int i=0;i<fre.size();i++)
        {
            frequentItemset.add(fre.get(i));
        }
        return true;
    }



    public static  void ShowData(List<List<String>> CandidateItemset)//显示出candidateitem中的所有的项集
    {
        for(int i=0;i<CandidateItemset.size();i++){
            List<String> list = new ArrayList<String>(CandidateItemset.get(i));
            for(int j=0;j<list.size();j++){
                System.out.print(list.get(j)+" ");
            }
            System.out.println();
        }
    }




    /**
     ******************************************************* 有当前频繁项集自连接求下一次候选集
     */
    private static List<List<String>> getNextCandidate(List<List<String>> FrequentItemset) {
        List<List<String>> nextCandidateItemset = new ArrayList<List<String>>();

        for (int i=0; i<FrequentItemset.size(); i++){
            HashSet<String> hsSet = new HashSet<String>();
            HashSet<String> hsSettemp = new HashSet<String>();
            for (int k=0; k< FrequentItemset.get(i).size(); k++)//获得频繁集第i行
                hsSet.add(FrequentItemset.get(i).get(k));
            int hsLength_before = hsSet.size();//添加前长度
            hsSettemp=(HashSet<String>) hsSet.clone();
            for(int h=i+1; h<FrequentItemset.size(); h++){//频繁集第i行与第j行(j>i)连接   每次添加且添加一个元素组成    新的频繁项集的某一行，
                hsSet=(HashSet<String>) hsSettemp.clone();//！！！做连接的hasSet保持不变
                for(int j=0; j< FrequentItemset.get(h).size();j++)
                    hsSet.add(FrequentItemset.get(h).get(j));
                int hsLength_after = hsSet.size();
                if(hsLength_before+1 == hsLength_after && isnotHave(hsSet,nextCandidateItemset)){
                    //如果不相等，表示添加了1个新的元素       同时判断其不是候选集中已经存在的一项
                    Iterator<String> itr = hsSet.iterator();
                    List<String>  tempList = new ArrayList<String>();
                    while(itr.hasNext()){
                        String Item = (String) itr.next();
                        tempList.add(Item);
                    }
                    nextCandidateItemset.add(tempList);
                }

            }

        }
        return nextCandidateItemset;
    }



    /**
     * 判断新添加元素形成的候选集是否在新的候选集中
     */
    private static boolean isnotHave(HashSet<String> hsSet, List<List<String>> nextCandidateItemset) {//判断hsset是不是candidateitemset中的一项
        List<String>  tempList = new ArrayList<String>();
        Iterator<String> itr = hsSet.iterator();
        while(itr.hasNext()){//将hsset转换为List<String>
            String Item = (String) itr.next();
            tempList.add(Item);
        }
        for(int i=0; i<nextCandidateItemset.size();i++)//遍历candidateitemset，看其中是否有和templist相同的一项
            if(tempList.equals(nextCandidateItemset.get(i)))
                return false;
        return true;
    }


    /**
     * 由k项候选集剪枝得到k项频繁集
     * @param CandidateItemset
     * @return
     */
    private static List<List<String>> getSupprotedItemset(List<List<String>> CandidateItemset) { //对所有的商品进行支持度计数
        // TODO Auto-generated method stub
        boolean end = true;
        List<List<String>> supportedItemset = new ArrayList<List<String>>();

        for (int i = 0; i < CandidateItemset.size(); i++){

            int count = countFrequent1(CandidateItemset.get(i));//统计记录数

            if (count >= MIN_SUPPROT * (record.size()-1)){
                supportedItemset.add(CandidateItemset.get(i));
                map.add(new Mymap(CandidateItemset.get(i),count));//存储当前频繁项集以及它的支持度计数
                end = false;
            }
        }
        endTag = end;//存在频繁项集则不会结束
        if(endTag==true)
            System.out.println("*****************无满足支持度的"+times+"项集,结束连接");
        return supportedItemset;
    }




    /**
     * 统计record中出现list集合的个数
     */
    private static int countFrequent1(List<String> list) {//遍历所有数据集record，对单个候选集进行支持度计数

        int count =0;
        for(int i=0;i<record.size();i++)//从record的第一个开始遍历
        {
            boolean flag=true;
            for (int j=0;j<list.size();j++)//如果record中的第一个数据集包含list中的所有元素
            {
                String t=list.get(j);
                if(!record.get(i).contains(t)) {
                    flag = false;
                    break;
                }
            }
            if(flag)
                count++;//支持度加一
        }

        return count;//返回支持度计数

    }

    /**
     * 获得一项候选集
     * @return
     */
    private static List<List<String>> findFirstCandidate() {
        // TODO Auto-generated method stub
        List<List<String>> tableList = new ArrayList<List<String>>();
        HashSet<String> hs  = new HashSet<String>();//新建一个hash表，存放所有的不同的一维数据

        for (int i = 1; i<record.size(); i++){  //遍历所有的数据集，找出所有的不同的商品存放到hs中
            for(int j=1;j<record.get(i).size();j++){
                hs.add(record.get(i).get(j));
            }
        }
        Iterator<String> itr = hs.iterator();
        while(itr.hasNext()){
            List<String>  tempList = new ArrayList<String>();
            String Item = (String) itr.next();
            tempList.add(Item);   //将每一种商品存放到一个List<String>中
            tableList.add(tempList);//所有的list<String>存放到一个大的list中
        }
        return tableList;//返回所有的商品
    }
}




class  Mymap{//自定义的map类，一个对象存放一个频繁项集以及其支持度计数
    public List<String> li=new LinkedList<>();
    public  int count;

    public Mymap(List<String> l,int c)//构造函数  新建一个对象
    {
        li=l;
        count=c;
    }

    public int getcount()//返回得到当前频繁项集的支持度计数
    {
        return count;
    }

    public boolean isListEqual(List<String> in)//判断传入的频繁项集是否和本频繁项集相同
    {
        if(in.size()!=li.size())//先判断大小是否相同
            return false;
        else {
            for(int i=0;i<in.size();i++)//遍历输入的频繁项集，判断是否所有元素都包含在本频繁项集中
            {
                if(!li.contains(in.get(i)))
                    return false;
            }
        }
        return true;//如果两个频繁项集大小相同，同时本频繁项集包含传入的频繁项集的所有元素，则表示两个频繁项集是相等的，返回为真
    }
}