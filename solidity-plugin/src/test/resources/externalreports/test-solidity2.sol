pragma solidity ^0.4.16;

contract WinnerTakesAll {
    uint minimumEntryFee;
    uint public deadlineProjects;
    uint public deadlineCampaign;
    uint public winningFunds;
    address public winningAddress;
    struct Project {
        address addr;
        string name;
        string url;
        uint funds;
        bool initialized;
    }
    mapping (address => Project) projects;
    address[] public projectAddresses;
    uint public numberOfProjects;
    event ProjectSubmitted(address addr, string name, string url, bool initialized);
    event ProjectSupported(address addr, uint amount);
    event PayedOutTo(address addr, uint winningFunds);

    function WinnerTakesAll(uint _minimumEntryFee, uint _durationProjects, uint _durationCampaign) public {
        if (_durationCampaign <= _durationProjects) {
            throw;
        }
        minimumEntryFee = _minimumEntryFee;
        deadlineProjects = now + _durationProjects* 1 seconds;
        deadlineCampaign = now + _durationCampaign * 1 seconds;
        winningAddress = msg.sender;
        winningFunds = 0;
    }
    function submitProject(string name, string url) payable public returns (bool success) {
        if (msg.value < minimumEntryFee) {
            throw;
        }
        if (now > deadlineProjects) {
            throw;
        }
        if (!projects[msg.sender].initialized) {
            projects[msg.sender] = Project(msg.sender, name, url, 0, true);

            projectAddresses.push(msg.sender);
            numberOfProjects = projectAddresses.length;
            ProjectSubmitted(msg.sender, name, url, projects[msg.sender].initialized);
            return true;
        }
        return false;
    }
    function supportProject(address addr) payable public returns (bool success) {
        if (msg.value <= 0) {
            throw;
        }
        if (now > deadlineCampaign || now <= deadlineProjects) {
            throw;
        }
        if (!projects[addr].initialized) {
            throw;
        }
        projects[addr].funds += msg.value;
        if (projects[addr].funds > winningFunds) {
            winningAddress = addr;
            winningFunds = projects[addr].funds;
        }
        ProjectSupported(addr, msg.value);
        return true;
    }
    function getProjectInfo(address addr) public constant returns (string name, string url, uint funds) {
        var project = projects[addr];
        if (!project.initialized) {
            throw;
        }
        return (project.name, project.url, project.funds);
    }
    function finish() {
        if (now >= deadlineCampaign) {
            PayedOutTo(winningAddress, winningFunds);
            selfdestruct(winningAddress);
        }
    }
}
