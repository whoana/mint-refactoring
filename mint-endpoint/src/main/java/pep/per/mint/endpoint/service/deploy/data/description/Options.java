package pep.per.mint.endpoint.service.deploy.data.description;

/**
 * <pre>
 *     모델배포 내용 커스텀을 위한 옵션 변수들 지정
 * </pre>
 * @author whoana
 * @since 2021.02
 */
public class Options {



    //서버정보 추가 여부
    boolean includeServerInfo = false;

    public boolean isIncludeServerInfo() {
        return includeServerInfo;
    }

    public void setIncludeServerInfo(boolean includeServerInfo) {
        this.includeServerInfo = includeServerInfo;
    }
}
