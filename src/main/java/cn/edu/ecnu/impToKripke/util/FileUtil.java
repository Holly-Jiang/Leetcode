package cn.edu.ecnu.impToKripke.util;

import java.io.*;
import java.util.*;

public class FileUtil {

    public static List<String> toArrayByInputStreamReader1(String name) {
        // 使用ArrayList来存储每行读取到的字符串
        List<String> arrayList = new ArrayList<>();
        try {
            File file = new File(name);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            StringBuffer sb=new StringBuffer();
            while ((str = bf.readLine()) != null) {
                if (str.contains(":")){
                    arrayList.add(sb.toString());
                    sb=new StringBuffer();
                    continue;
                }
                sb.append(" ");
                sb.append(str);
            }
            arrayList.add(sb.toString());
            //delete cobegin ...coend
            if(arrayList.size()>0){
                arrayList.remove(0);
            }
            bf.close();
            inputReader.close();
            return arrayList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
        //test
    public static void main(String[] args) {
     FileUtil fileUtil = new FileUtil();
        int[] nums={1,2,4};
        System.out.println(fileUtil.generateMatrix(2));

    }
    public int[][] generateMatrix(int n) {
        int count=1;
        int [][] num=new int[n][n];
        int row=0,col=0;
        boolean rowFlag1=true,rowFlag2=true,colFlag1=true,colFlag2=true;
        for(int i=0;i<n;i++){
            row=i;col=i;
            while(row>=i&&row<n-i&&col>=i&&col<n-i) {
                if (row == i && col < n - i) {
                    num[row][col] = count++;
                    col++;
                    if (col == n - i) {
                        col--;
                        row++;
                    }
                    if (row == i&&col==i) {
                        break;
                    }

                } else if (col == n - i - 1 && row < n - i) {
                    num[row][col] = count++;
                    row++;
                    if (row == n - i) {
                        row--;
                        col--;
                    }
                    if (row == i&&col==i) {
                        break;
                    }
                } else if (row == n - i - 1 && col >= i) {
                    num[row][col] = count++;
                    col--;
                    if (col == i - 1) {
                        col++;
                        row--;
                    }
                    if (row == i&&col==i) {
                        break;
                    }
                } else if (col == i && row > i) {
                    num[row][col] = count++;
                    row--;

                    if (row == i&&col==i) {
                      break;
                    }
                }
            }
            }
        return num;
    }



    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int len=nums.length;
        int min=0, res=0,left=0,right=0;
        for(int i=0;i<len;i++){
            for(int j=len;j>=0;j--){
                if(Math.sqrt(target-nums[i]+nums[j]+nums[0])-target>0){
                    left=i;right=j;
                }else if (Math.sqrt(target-nums[i]+nums[j]+nums[0])-min<0){
                    min=(int)(target-Math.sqrt(target-nums[i]+nums[j]+nums[0]));
                }
            }
        }

        for(int i=left;i<len;i++){
            for(int j=right;j>=0;j--){
                if(Math.sqrt(target-nums[i]+nums[j]+nums[0])-target>0) {
                    left = i;
                    right = j;
                }
            }
        }
        return res;
    }


    int []res;
    public int maxProfit(int[] prices) {

        int len=prices.length;
        res=new int[len];
        int max=0;

        for(int i=0;i<len;i++) {
            get(i, prices);
        }
        for(int i=0;i<len;i++){
            if(max<res[i]){
                max=res[i];
            }
        }
        return max;
    }
    public void get(int start,int []prices){
        int nstar=start,end=start,max=0;
        for(int j=start+1;j<prices.length;j++){
            if(prices[j]<prices[start]){
                start=j;
            }else{
                end=j;
                max=0;
                if(nstar-2>=0){
                    max+=res[nstar-2];

                }
                max+=prices[end]-prices[start];
                if(res[j]<max){
                    res[j]=max;

                }
            }


        }
    }

}
